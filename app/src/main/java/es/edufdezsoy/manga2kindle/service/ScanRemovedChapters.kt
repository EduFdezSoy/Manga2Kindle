package es.edufdezsoy.manga2kindle.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.documentfile.provider.DocumentFile
import es.edufdezsoy.manga2kindle.M2kApplication
import es.edufdezsoy.manga2kindle.data.M2kDatabase
import es.edufdezsoy.manga2kindle.ui.newChapters.NewChaptersInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ScanRemovedChapters : JobIntentService(), CoroutineScope {
    private val TAG = M2kApplication.TAG + "_ScanRmCh"
    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    companion object {
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, ScanRemovedChapters::class.java, 0, intent)
        }
    }

    override fun onHandleWork(intent: Intent) {
        Log.d(TAG, "Service ScanRemovedChapters started.")
        job = Job()
        val database = M2kDatabase.invoke(this)

        launch {
            database.ChapterDao().getNoUploadedChapters().also {
                it.forEach {
                    val docFile = DocumentFile.fromSingleUri(
                        this@ScanRemovedChapters,
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

            val broadcastIntent = Intent()
            broadcastIntent.action =
                NewChaptersInteractor.ServiceReceiver.ACTION_UPDATED_CHAPTER_LIST
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT)
            sendBroadcast(broadcastIntent)
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "Service ScanRemovedChapters destroyed.")
    }
}