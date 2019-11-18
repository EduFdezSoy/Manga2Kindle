package es.edufdezsoy.manga2kindle.service.intentService

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import es.edufdezsoy.manga2kindle.M2kApplication
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.data.repository.ChapterRepository
import es.edufdezsoy.manga2kindle.network.ApiService
import es.edufdezsoy.manga2kindle.service.util.BroadcastReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext


class UpdateChapterStatusIntentService : JobIntentService(), CoroutineScope {
    private val TAG = M2kApplication.TAG + "_UpdateChSt"
    private val broadcastIntent = Intent()
    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    private val chapterRepository = ChapterRepository.invoke(this)

    companion object {
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, UpdateChapterStatusIntentService::class.java, 3, intent)
        }
    }

    init {
        broadcastIntent.action = BroadcastReceiver.ACTION_UPDATED_CHAPTER_STATUS
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT)
    }

    override fun onHandleWork(workIntent: Intent) {
        Log.d(TAG, "Service UpdateChapterStatusIntentService started.")
        job = Job()
        val apiService = ApiService.apiService
        // TODO: move all apiService to the repositories

        launch {
            chapterRepository.getUploadedChapters().also {
                it.forEach { chapIn ->
                    var chapter = chapIn
                    if (mayCheckStatus(chapter)) {
                        try {
                            Log.d(TAG, chapter.toString())
                            apiService.getStatus(chapter.id!!).also {
                                if (it.isNotEmpty()) {
                                    chapter.status = Chapter.STATUS_UPLOADED
                                    chapter.delivered = it[0].delivered
                                    chapter.error = it[0].error
                                    chapter.reason = it[0].reason

                                    Log.d(
                                        TAG,
                                        "Manga." + it[0].manga_id + " Ch." + it[0].chapter + " Title: " + it[0].title
                                    )
                                } else {
                                    if (M2kApplication.debug)
                                        Log.w(TAG, "CHAPTER STATUS EMPTY")
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "ERROR RETRIEVING THE CHAPTER STATUS")
                            if (M2kApplication.debug)
                                e.printStackTrace()
                        } finally {
                            chapter = checkLocalFail(chapter)
                            chapterRepository.update(chapter)
                        }
                    }
                }

                // TODO: changing repo lists to observable lists we can remove those broadcasts
                sendBroadcast(broadcastIntent)
            }
        }
    }

    /**
     * If the chapter keep waiting in the local storage, mark as local error,
     * always return the same chapter
     */
    private fun checkLocalFail(chapter: Chapter): Chapter {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MINUTE, -1)
        val compareDate = cal.time

        if (chapter.upload_date != null)
            if (chapter.upload_date!!.before(compareDate))
                if (chapter.status != Chapter.STATUS_LOCAL_ERROR && chapter.status != Chapter.STATUS_UPLOADED)
                    chapter.status = Chapter.STATUS_LOCAL_ERROR

        return chapter
    }

    private fun mayCheckStatus(chapter: Chapter): Boolean {
        val cal = Calendar.getInstance()
        cal.add(Calendar.HOUR, -1)
        val compareDate = cal.time

        if (chapter.upload_date == null)
            return false

        if (chapter.upload_date!!.after(compareDate)) {
            if (!chapter.delivered)
                return true
        }

        if (!chapter.delivered)
            if (!chapter.error)
                if (chapter.status != Chapter.STATUS_LOCAL_ERROR && chapter.status != Chapter.STATUS_DEFAULT)
                    return true

        return false
    }

    override fun onDestroy() {
        Log.d(TAG, "Service UpdateChapterStatusIntentService destroyed.")
    }
}