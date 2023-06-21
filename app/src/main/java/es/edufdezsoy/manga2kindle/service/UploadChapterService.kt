package es.edufdezsoy.manga2kindle.service

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.documentfile.provider.DocumentFile
import es.edufdezsoy.manga2kindle.Application
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.model.ChapterWithManga
import es.edufdezsoy.manga2kindle.data.model.Manga
import es.edufdezsoy.manga2kindle.data.model.Status
import es.edufdezsoy.manga2kindle.data.model.UploadChapter
import es.edufdezsoy.manga2kindle.data.repository.ChapterRepository
import es.edufdezsoy.manga2kindle.data.repository.MangaRepository
import es.edufdezsoy.manga2kindle.data.repository.SharedPreferencesHandler
import es.edufdezsoy.manga2kindle.network.ApiService
import es.edufdezsoy.manga2kindle.network.Manga2KindleService
import es.edufdezsoy.manga2kindle.network.adapters.PrepareQueryParamsAdapter
import es.edufdezsoy.manga2kindle.utils.Log
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
 * TODO: Service  incomplete MAKE IT A SINGLETON and edit this text
 * This service may be launch when chapters needs to be uploaded, uses a notification as it needs to
 * be launched as fast as possible and needs to keep working even if the user closes the app to
 * deliver the chapters.
 *
 * In order to reduce network problems to a minimum chapters wont be uploaded as a zip, instead we
 * will upload pages one by one. If one fails we only retry that one.
 *
 * By doing it one by one we can also stop the process at any time and keep track of the % completed
 *
 */
class UploadChapterService : Service(), CoroutineScope {
    //region vars and vals
    private val TAG = this::class.java.simpleName
    private val binder = UploadChapterBinder()
    private val chapList = ArrayList<ChapterWithManga>()
    private lateinit var apiService: Manga2KindleService
    private lateinit var chapterRepository: ChapterRepository
    private lateinit var mangaRepository: MangaRepository
    private var listSize = 0
    private var iteration = 0
    private lateinit var notificationManager: NotificationManagerCompat

    @Volatile
    private var atomicBooleanJobDone = false

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
        val element = intent?.extras?.get(UPLOAD_CHAPTER_INTENT_KEY) as ChapterWithManga?
        if (element != null) {
            chapList.add(element)
            listSize++

            // set the chapter as locally enqueued
            launch {
                element.chapter.status = Status.REGISTERED
                chapterRepository.update(element.chapter)
            }
        }

        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        chapterRepository = ChapterRepository(application)
        mangaRepository = MangaRepository(application)

        // TODO: work with ChapterWithManga, use only UploadChapter and UploadManga here to put a manga and link with the chapter
        val element = intent?.extras?.get(UPLOAD_CHAPTER_INTENT_KEY) as ChapterWithManga?
        if (element != null) {
            chapList.add(element)
            listSize++

            // set the chapter as locally enqueued
            launch {
                element.chapter.status = Status.REGISTERED
                chapterRepository.update(element.chapter)
            }
        }

        if (atomicBooleanJobDone) {
            return START_REDELIVER_INTENT
        }

        atomicBooleanJobDone = true

        apiService = ApiService.getInstance(applicationContext)
        notificationManager = NotificationManagerCompat.from(this)

        val notification = NotificationCompat.Builder(this, Application.CHANNEL_ID).apply {
            setContentTitle("Uploading Chapter") // TODO: move to a resource
            setProgress(0, 0, true)
            setSmallIcon(R.drawable.ic_tpose) // TODO: change icon
            priority = NotificationCompat.PRIORITY_LOW
        }

        startForeground(2, notification.build()) // TODO: move id to a resource

        launch {
            while (chapList.size > 0) {
                val chapterWithManga = chapList[0]
                var progress = 0
                val progressMax = 0

                notification.setContentText("${++iteration} of $listSize") // TODO: move to a resource and translate
                notification.setProgress(progressMax, progress, false)

                try {
                    if (ActivityCompat.checkSelfPermission(
                            applicationContext,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        val filler = "filler"
                    }
                    notificationManager.notify(2, notification.build())
                } catch (_: Exception) {
                    android.util.Log.e(
                        TAG,
                        "UploadChapterService:151 -> Cant show notification :(",
                    )
                }

                // VVVVVV -- GOOD CODE HERE -- VVVVVV

                // update chapter status
                chapterWithManga.chapter.status = Status.UPLOADING
                chapterRepository.update(chapterWithManga.chapter)

                // search and/or upload manga if not present
                if (chapterWithManga.manga.uuid.isNullOrBlank()) {
                    // search in server
                    val mangaListRes = apiService.searchManga(PrepareQueryParamsAdapter("title", chapterWithManga.manga.title).toString())
                    if (mangaListRes.items.isEmpty()) {
                        // register to server
                        val mangaRes = apiService.putManga(chapterWithManga.manga)

                        // update repo
                        mangaRes.mangaId = chapterWithManga.manga.mangaId
                        mangaRepository.update(mangaRes)

                        // update local
                        chapterWithManga.manga.uuid = mangaRes.uuid
                    } else {
                        // update repo
                        mangaListRes.items[0].mangaId = chapterWithManga.manga.mangaId
                        mangaRepository.update(mangaListRes.items[0])

                        // update local
                        chapterWithManga.manga = mangaListRes.items[0]
                    }
                }

                // create uploadChapter
                val uploadChapter = UploadChapter(
                    chapterWithManga,
                    SharedPreferencesHandler(applicationContext).kindleEmail,
                    SharedPreferencesHandler(applicationContext).readMode,
                    SharedPreferencesHandler(applicationContext).splitType
                )

                // put new chapter to server
                var uploadChapterRes = apiService.putChapter(uploadChapter)

                // update local chapter id
                chapterWithManga.chapter.id = uploadChapterRes.id
                chapterRepository.update(chapterWithManga.chapter)

                // get perms to file
                val res = getDocReadableFilePath(
                    DocumentFile.fromSingleUri(
                        applicationContext,
                        Uri.parse(chapterWithManga.chapter.path)
                    ), applicationContext
                )
                android.util.Log.d(TAG, "onStartCommand: $res")

                // create multipart body
                val mpBody = MultipartBody.Part.createFormData(
                    "file",
                    "${uploadChapterRes.id}.cbz",
                    RequestBody.create(
                        MediaType.parse("zip"),
                        File(chapterWithManga.chapter.path)
                    )
                )

                // upload chapter file
                uploadChapterRes = apiService.putChapterFile(uploadChapterRes.id!!, mpBody)

                // update chapter status
                chapterWithManga.chapter.status = Status.AWAITING
                chapterRepository.update(chapterWithManga.chapter)

//
//                // TODO: create chapter, upload chapter metadata
//                val uploadChapterCopy = uploadChapter.copy()
//                uploadChapterCopy.path = null
//                uploadChapterCopy.id = null
//                val status = apiService.getNewChapterStatus(uploadChapterCopy)
//                val chapter = chapterRepository.getById(uploadChapter.rowid)
//
//                chapter!!.id = status.id
//                chapter.status = status.id!!
//                chapterRepository.update(chapter)
//
//                val fileName = status.id

                notification.setContentText("$iteration of $listSize") // TODO: move to a resource and translate
                notification.setProgress(progressMax, ++progress, false)

//                // upload image
//                apiService.putChapterFile(
//                    status.id!!,
//                    MultipartBody.Part.createFormData(
//                        "file",
//                        "$fileName.cbz",
//                        RequestBody.create(
//                            MediaType.parse("zip"),
//                            File(chapter.path)
//                        )
//                    )
//                )

                // TODO: we don't handle any errors here now, we may implement something to know
                //  what pages are uploaded and continue from that point.
                //  Maybe an API call can reply if the page exists, that looks easy to implement.

                notification.setProgress(progressMax, progress, false)
                notificationManager.notify(2, notification.build())

                // remove from list when done
                chapList.remove(chapterWithManga)
            }

            atomicBooleanJobDone = false
            stopSelf()
        }

        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        notificationManager.cancel(2)
        atomicBooleanJobDone = false
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
     * @deprecated use {@link #getDocumentFile()} instead.
     *
     * @param uri folder to be searched
     * @param context the context
     * @return a list of files inside the given uri, can be empty
     */
    @Deprecated("We don't use List<DocumentFile> anymore.", ReplaceWith("getDocumentFile()"))
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

    fun addUploadChapter(chapter: ChapterWithManga) {
        chapList.add(chapter)
        listSize++
    }

    //endregion
}