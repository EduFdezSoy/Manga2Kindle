package es.edufdezsoy.manga2kindle.service.intentService

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.documentfile.provider.DocumentFile
import es.edufdezsoy.manga2kindle.M2kApplication
import es.edufdezsoy.manga2kindle.data.M2kDatabase
import es.edufdezsoy.manga2kindle.service.util.BroadcastReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ScanRemovedChaptersIntentService : JobIntentService(), CoroutineScope {
    private val TAG = M2kApplication.TAG + "_ScanRmCh"
    private val broadcastIntent = Intent()
    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    companion object {
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, ScanRemovedChaptersIntentService::class.java, 0, intent)
        }
    }

    init {
        broadcastIntent.action = BroadcastReceiver.ACTION_UPDATED_CHAPTER_LIST
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT)
    }

    override fun onHandleWork(intent: Intent) {
        Log.d(TAG, "Service ScanRemovedChaptersIntentService started.")
        job = Job()
        val database = M2kDatabase.invoke(this)

        launch {
            database.ChapterDao().getNoUploadedChapters().also {
                it.forEach {
                    val docFile = DocumentFile.fromSingleUri(
                        this@ScanRemovedChaptersIntentService,
                        Uri.parse(it.file_path)
                    )

                    if (docFile == null || !docFile.exists()) {
                        if (M2kApplication.debug)
                            Log.d(
                                TAG, "Removed manga: " + it.manga_id +
                                        " Chapter: " + it.chapter +
                                        " - " + it.title
                            )

                        database.ChapterDao().delete(it)
                    }
                }
            }

            sendBroadcast(broadcastIntent)
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "Service ScanRemovedChaptersIntentService destroyed.")
    }
}