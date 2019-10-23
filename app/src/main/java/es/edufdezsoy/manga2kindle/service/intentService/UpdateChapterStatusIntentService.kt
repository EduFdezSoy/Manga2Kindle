package es.edufdezsoy.manga2kindle.service.intentService

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import androidx.core.app.JobIntentService
import es.edufdezsoy.manga2kindle.M2kApplication
import es.edufdezsoy.manga2kindle.data.M2kDatabase
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.network.ApiService
import es.edufdezsoy.manga2kindle.service.util.BroadcastReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext


class UpdateChapterStatusIntentService : JobIntentService(), CoroutineScope {
    private val TAG = M2kApplication.TAG + "_UpdateChSt"
    private val broadcastIntent = Intent()
    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    private val handler = Handler()
    private val database = M2kDatabase.invoke(this)
    private val pendingFailChapters = ArrayList<Chapter>()

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

        launch {
            database.ChapterDao().getUploadedChapters().also {
                it.forEach { chapter ->
                    val cal = Calendar.getInstance()
                    cal.add(Calendar.HOUR, -1)
                    val compareDate = cal.time
                    if (!chapter.delivered && !chapter.error && chapter.upload_date!!.after(compareDate)) {
                        try {
                            Log.d(TAG, chapter.toString())
                            apiService.getStatus(chapter.id!!).also {
                                if (it.isNotEmpty()) {
                                    if (it[0].delivered || it[0].error) {
                                        chapter.status = 3
                                        chapter.delivered = it[0].delivered
                                        chapter.error = it[0].error

                                        Log.d(
                                            TAG,
                                            "Manga." + it[0].manga_id + " Ch." + it[0].chapter + " Title: " + it[0].title
                                        )
                                        database.ChapterDao().update(chapter)
                                    }
                                } else {
                                    registerToCheckIfFailed(chapter)

                                    if (M2kApplication.debug)
                                        Log.w(TAG, "CHAPTER STATUS EMPTY")
                                }
                            }
                        } catch (e: Exception) {
                            registerToCheckIfFailed(chapter)

                            Log.e(TAG, "ERROR RETRIEVING THE CHAPTER STATUS")
                            if (M2kApplication.debug)
                                e.printStackTrace()
                        }
                    }
                }

                sendBroadcast(broadcastIntent)
            }
        }
    }


    private suspend fun registerToCheckIfFailed(chapter: Chapter) {
        if (!pendingFailChapters.contains(chapter)) {
            if (chapter.status == 3) {
                chapter.status = 4
                database.ChapterDao().update(chapter)
                pendingFailChapters.remove(chapter)
            } else {
                pendingFailChapters.add(chapter)
                handler.postDelayed({ checkLocalFail(chapter.identifier) }, 60000)
            }
        }
    }

    private fun checkLocalFail(chapter_id: Int) {
        launch {
            database.ChapterDao().getChapter(chapter_id).also {
                if (it.status != 4 && it.status != 3) {
                    it.status = 4
                    database.ChapterDao().update(it)
                }
                pendingFailChapters.remove(it)
            }
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "Service UpdateChapterStatusIntentService destroyed.")
    }
}