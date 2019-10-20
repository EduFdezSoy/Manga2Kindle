package es.edufdezsoy.manga2kindle.service.intentService

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import es.edufdezsoy.manga2kindle.M2kApplication
import es.edufdezsoy.manga2kindle.data.M2kDatabase
import es.edufdezsoy.manga2kindle.network.ApiService
import es.edufdezsoy.manga2kindle.service.util.BroadcastReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class UpdateChapterStatusIntentService : JobIntentService(), CoroutineScope {
    private val TAG = M2kApplication.TAG + "_UpdateChSt"
    private val broadcastIntent = Intent()
    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

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
        val database = M2kDatabase.invoke(this)
        val apiService = ApiService.apiService

        launch {
            database.ChapterDao().getUploadedChapters().also {
                it.forEach { chapter ->
                    if (!chapter.delivered && !chapter.error) {
                        apiService.getStatus(chapter.id!!).also {
                            if (it.isNotEmpty())
                                if (it[0].delivered || it[0].error) {
                                    chapter.delivered = it[0].delivered
                                    chapter.error = it[0].error

                                    Log.d(TAG, it[0].toString())
                                    database.ChapterDao().update(chapter)
                                }
                        }
                    }
                }

                sendBroadcast(broadcastIntent)
            }
        }
    }


    override fun onDestroy() {
        Log.d(TAG, "Service UpdateChapterStatusIntentService destroyed.")
    }
}