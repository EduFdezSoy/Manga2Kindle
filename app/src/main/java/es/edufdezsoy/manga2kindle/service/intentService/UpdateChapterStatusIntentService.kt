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
            enqueueWork(context, UpdateChapterStatusIntentService::class.java, 0, intent)
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
                it.forEach { chapter ->
                    if (mayCheckStatus(chapter)) {
                        try {
                            Log.d(TAG, chapter.toString())
                            apiService.getStatus(chapter.id!!).also {
                                if (it.isNotEmpty()) {
                                    chapter.status = 3
                                    chapter.delivered = it[0].delivered
                                    chapter.error = it[0].error

                                    Log.d(
                                        TAG,
                                        "Manga." + it[0].manga_id + " Ch." + it[0].chapter + " Title: " + it[0].title
                                    )
                                } else {
                                    checkLocalFail(chapter)

                                    if (M2kApplication.debug)
                                        Log.w(TAG, "CHAPTER STATUS EMPTY")
                                }
                            }
                        } catch (e: Exception) {
                            checkLocalFail(chapter)

                            Log.e(TAG, "ERROR RETRIEVING THE CHAPTER STATUS")
                            if (M2kApplication.debug)
                                e.printStackTrace()
                        } finally {
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
                if (chapter.status != 4 && chapter.status != 3)
                    chapter.status = 4

        return chapter
    }

    /**
     * Checks the chapter only when it hasn't been delivered AND it has no errors
     * AND it was uploaded between an hour ago and now OR the chapter status is
     * between 4 and 0 (1, 2 or 3).
     */
    private fun mayCheckStatus(chapter: Chapter): Boolean {
        val cal = Calendar.getInstance()
        cal.add(Calendar.HOUR, -1)
        val compareDate = cal.time

        if (!chapter.delivered)
            if (!chapter.error)
                if (chapter.upload_date!!.after(compareDate) || (chapter.status < 4 && chapter.status != 0))
                    return true

        return false
    }

    override fun onDestroy() {
        Log.d(TAG, "Service UpdateChapterStatusIntentService destroyed.")
    }
}