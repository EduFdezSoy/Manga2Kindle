package es.edufdezsoy.manga2kindle.service.intentService

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.app.JobIntentService
import es.edufdezsoy.manga2kindle.M2kApplication
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.data.repository.AuthorRepository
import es.edufdezsoy.manga2kindle.data.repository.ChapterRepository
import es.edufdezsoy.manga2kindle.data.repository.MangaRepository
import es.edufdezsoy.manga2kindle.network.ProgressRequestBody
import es.edufdezsoy.manga2kindle.service.UploadChapterUtils
import es.edufdezsoy.manga2kindle.service.util.BroadcastReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.*
import kotlin.coroutines.CoroutineContext

class UploadChapterIntentService : JobIntentService(), CoroutineScope,
    ProgressRequestBody.UploadCallbacks {
    private val TAG = M2kApplication.TAG + "_UpChapISrv"
    private val broadcastIntent = Intent()
    private val job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    companion object {
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, UploadChapterIntentService::class.java, 4, intent)
        }
    }

    init {
        broadcastIntent.action = BroadcastReceiver.ACTION_UPLOADED_CHAPTER
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT)
    }

    override fun onHandleWork(intent: Intent) {
        Log.v(TAG, "Service UploadChapterIntentService created")
        launch {
            val context: Context = this@UploadChapterIntentService
            val chapterRepository = ChapterRepository.invoke(context)
            val mangaRepository = MangaRepository.invoke(context)
            val authorRepository = AuthorRepository.invoke(context)

            val sharedPref = context.getSharedPreferences(
                "es.edufdezsoy.manga2kindle_preferences",
                Context.MODE_PRIVATE
            )
            val mail = sharedPref.getString("kindle_mail", null)
            if (mail.isNullOrBlank()) {
                chapterRepository.getEnqueuedChapters().forEach {
                    it.status = Chapter.STATUS_LOCAL_ERROR
                    it.reason = "There is no mail set to send it!"
                }
            } else
                do {
                    val chapterQueue = chapterRepository.getEnqueuedChapters()
                    chapterQueue.sortBy { it.enqueue_date }

                    chapterQueue.forEach {
                        // start processing the chapter!
                        it.status = Chapter.STATUS_PROCESSING
                        chapterRepository.update(it)

                        var manga = mangaRepository.getMangaById(it.manga_id)
                        manga = UploadChapterUtils.syncManga(manga, context)

                        // manga exists now and is in sync with the server and all that crap.
                        // check other chapter stuff
                        if (it.lang_id == null) {
                            // TODO("launch an exception") // for now it will be EN always
                            it.lang_id = 1 // 1 EN, 2 ES == https://manga2kindle.com/languages
                        }

                        if (it.file_path == null) {
                            throw IllegalArgumentException("There is no path to this chapter, probably it was deleted from the disk")
                        }

                        // lets compress the chapter files
                        val zipName = UploadChapterUtils.cleanTextContent(manga.title) + " Ch." +
                                UploadChapterUtils.trimTrailingZero(it.chapter.toString()) + ".zip"
                        UploadChapterUtils.zip(Uri.parse(it.file_path), zipName, context)

                        // get the checksum (md5)
                        val chapFile = File(context.filesDir, zipName)
                        try {
                            it.checksum = UploadChapterUtils.calculateMD5(FileInputStream(chapFile))
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                            it.status = Chapter.STATUS_LOCAL_ERROR
                            it.reason = "Looks like this file is too big!"
                            chapterRepository.update(it)
                        }

                        // prepare other fields
                        var title = it.title
                        if (title == null)
                            title = ""

                        // set chapter to uploading
                        it.status = Chapter.STATUS_UPLOADING
                        it.upload_date = Calendar.getInstance().time
                        chapterRepository.update(it)

                        val fileBody =
                            ProgressRequestBody(chapFile, this@UploadChapterIntentService)
                        val part =
                            MultipartBody.Part.createFormData("file", chapFile.name, fileBody)

                        chapterRepository.sendChapter(
                            manga.id!!,
                            it.lang_id!!,
                            title,
                            it.chapter,
                            it.volume,
                            it.checksum!!,
                            mail,
                            part
                        ).also { chapSrv ->
                            if (chapSrv != null) {
                                it.id = chapSrv.id
                                it.status = Chapter.STATUS_UPLOADED
                                chapterRepository.update(it)
                            } else {
                                it.status = Chapter.STATUS_LOCAL_ERROR
                                it.reason = "Upload failed"
                                chapterRepository.update(it)
                            }
                            // remove the chapter
                            chapFile.delete()
                        }
                    }
                } while (chapterQueue.isNotEmpty())

            // Launch broadcast
            sendBroadcast(broadcastIntent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        job.cancel()
        Log.v(TAG, "Service UploadChapterIntentService canceled/destroyed")

    }

    override fun onProgressUpdate(percentage: Int) {
        Log.v(TAG, "Service UploadChapterIntentService $percentage%")
    }

    override fun onError() {
        Log.v(TAG, "Service UploadChapterIntentService error!")
    }

    override fun onFinish() {
        Log.v(TAG, "Service UploadChapterIntentService 100% - finished")
    }
}