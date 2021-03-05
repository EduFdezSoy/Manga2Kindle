package es.edufdezsoy.manga2kindle.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.documentfile.provider.DocumentFile
import es.edufdezsoy.manga2kindle.Application
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.model.UploadChapter
import es.edufdezsoy.manga2kindle.data.repository.ChapterRepository
import es.edufdezsoy.manga2kindle.network.ApiService
import es.edufdezsoy.manga2kindle.network.Manga2KindleService
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileNotFoundException
import kotlin.coroutines.CoroutineContext

/**
 * TODO: Service  incomplete
 * This service may be launch when chapters needs to be uploaded, uses a notification as it needs to
 * be launched as fast as possible and needs to keep working even if the user closes the app to
 * deliver the chapters.
 *
 * In order to reduce network problems to a minimum chapters wont be uploaded as a zip, instead we
 * will upload pages one by one. If one fails we only retry that one.
 *
 * By doing it one by one we can also stop the process at any time and keep track of the % completed
 *
 * We will want the images already compressed and scaled
 */
class UploadChapterService : Service(), CoroutineScope {
    //region vars and vals
    private val TAG = this::class.java.simpleName
    private val binder = UploadChapterBinder()
    private val chapList = ArrayList<UploadChapter>()
    private lateinit var apiService: Manga2KindleService
    private lateinit var chapterRepository: ChapterRepository
    private var listSize = 0
    private var iteration = 0
    private lateinit var notificationManager: NotificationManagerCompat

    inner class UploadChapterBinder : Binder() {
        fun getService(): UploadChapterService = this@UploadChapterService
    }

    companion object {
        const val UPLOAD_CHAPTER_INTENT_KEY =
            "UploadChapterService_UploadChapter_List_Intent_Extra_Key"
    }

    //endregion
    //region override methods

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    override fun onBind(intent: Intent?): IBinder {
        val element = intent?.extras?.get(UPLOAD_CHAPTER_INTENT_KEY) as UploadChapter?
        if (element != null) {
            chapList.add(element)
            listSize++
        }

        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        chapterRepository = ChapterRepository(application)
        apiService = ApiService.getInstance(applicationContext)
        notificationManager = NotificationManagerCompat.from(this)

        val element = intent?.extras?.get(UPLOAD_CHAPTER_INTENT_KEY) as UploadChapter?
        if (element != null) {
            chapList.add(element)
            listSize++
        }

        val notification = NotificationCompat.Builder(this, Application.CHANNEL_ID).apply {
            setContentTitle("Uploading Chapter") // TODO: move to a resource
            setProgress(0, 0, true)
            setSmallIcon(R.drawable.ic_tpose) // TODO: change icon
            priority = NotificationCompat.PRIORITY_LOW
        }

        startForeground(2, notification.build()) // TODO: move id to a resource

        launch {
            chapList.forEach { uploadChapter ->
                var progress = 0
                var progressMax = 0

                notification.setContentText("${++iteration} of $listSize") // TODO: move to a resource and translate
                notification.setProgress(progressMax, progress, false)
                notificationManager.notify(2, notification.build())

                // get number of pages
                val fileList =
                    getFileList(
                        Uri.parse(uploadChapter.path),
                        applicationContext
                    )
                uploadChapter.pages = fileList.size
                uploadChapter.readMode = "manga" // TODO: Move to preferences
                uploadChapter.splitType = 0 // TODO: Move to preferences
                progressMax = fileList.size * 2

                // TODO: create chapter, upload chapter metadata
                val uploadChapterCopy = uploadChapter.copy()
                uploadChapterCopy.path = null
                uploadChapterCopy.id = null
                val status = apiService.getNewChapterStatus(uploadChapterCopy)
                val chapter = chapterRepository.getById(uploadChapter.id!!)
                chapter!!.remoteId = status.id
                chapter.status = status.status
                chapterRepository.update(chapter)

                var page = 0
                fileList.forEach {
                    notification.setProgress(progressMax, ++progress, false)
                    notificationManager.notify(2, notification.build())

                    // optimize images and convert to jpg
                    val pageImage =
                        Compressor.compress(
                            applicationContext,
                            File(getDocReadableFilePath(it, applicationContext))
                        ) {
                            default(format = Bitmap.CompressFormat.JPEG)
                        }

                    notification.setProgress(progressMax, ++progress, false)
                    notificationManager.notify(2, notification.build())

                    // upload image
                    apiService.putChapterPage(
                        status.id,
                        ++page,
                        MultipartBody.Part.createFormData(
                            "file",
                            "$page.jpg",
                            RequestBody.create(MediaType.parse("jpg"), pageImage)
                        )
                    )

                    // TODO: we don't handle any errors here now, we may implement something to know
                    //  what pages are uploaded and continue from that point.
                    //  Maybe an API call can reply if the page exists, that looks easy to implement.
                }

                notification.setProgress(progressMax, progress, false)
                notificationManager.notify(2, notification.build())

                // remove from list when done
                chapList.remove(uploadChapter)
            }

            stopSelf()
        }

        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        notificationManager.cancel(2)
        super.onDestroy()
    }

    //endregion
    //region private methods

    private fun getDocReadableFilePath(mDocFile: DocumentFile?, context: Context): String {
        if (mDocFile != null && mDocFile.isFile) {
            return try {
                val parcelFileDescriptor = context.contentResolver.openFileDescriptor(
                    mDocFile.uri,
                    "r"
                ) // gets FileNotFoundException here, if file we used to have was deleted
                if (parcelFileDescriptor != null) {
                    val fd = parcelFileDescriptor.detachFd() // if we want to close in native code
                    "/proc/self/fd/$fd"
                } else {
                    ""
                }
            } catch (fne: FileNotFoundException) {
                ""
            }
        } else {
            return ""
        }
    }

    /**
     * Get a list of files inside a folder
     *
     * @param uri folder to be searched
     * @param context the context
     * @return a list of files inside the given uri, can be empty
     */
    private fun getFileList(uri: Uri, context: Context): List<DocumentFile> {
        val docFileList = ArrayList<DocumentFile>()

        val badDocFile = DocumentFile.fromTreeUri(context, uri)

        if (badDocFile != null && badDocFile.isDirectory && badDocFile.canRead()) {

            // WORKAROUND: iterate folders inside that until the uri match with our one
            val docFile = getTheRightDocFile(badDocFile, uri)

            docFile?.listFiles()?.forEach {
                if (it.isFile && it.name != ".nomedia")
                    if (it.parentFile!!.name != ".thumb")
                        docFileList.add(it)
            }
        } else {
            Log.e(TAG, "Can't read the folder, is null or is not a folder. \n Folder: $uri")
        }

        return docFileList
    }

    /**
     * WORKAROUND: iterate folders inside that until the uri match with our one
     * // TODO: WORKAROUND, need a fix if answered: https://stackoverflow.com/questions/58078606/documentfile-not-opening-the-correct-uri
     *
     * @param docFile folder to be iterated
     * @param uri uri to be searched in the folder
     * @return a docFile if it match with the uri or null it it cant find it
     */
    private fun getTheRightDocFile(docFile: DocumentFile, uri: Uri): DocumentFile? {
        docFile.listFiles().forEach {
            if (it.isDirectory) {
                if (it.uri.toString() == uri.toString()) {
                    return it
                } else {
                    val df = getTheRightDocFile(it, uri)
                    if (df != null && df.isDirectory) {
                        if (df.uri.toString() == uri.toString()) {
                            return df
                        }
                    }
                }

            }
        }
        return null
    }
    //endregion
    //region public methods

    fun addUploadChapter(chapter: UploadChapter) {
        chapList.add(chapter)
        listSize++
    }

    //endregion
}