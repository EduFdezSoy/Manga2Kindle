package es.edufdezsoy.manga2kindle.service.intentService

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import es.edufdezsoy.manga2kindle.M2kApplication
import es.edufdezsoy.manga2kindle.service.ScanManga
import es.edufdezsoy.manga2kindle.service.util.BroadcastReceiver

class ScanMangaIntentService : JobIntentService() {
    private val broadcastIntent = Intent()

    companion object {
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, ScanMangaIntentService::class.java, 0, intent)
        }
    }

    init {
        broadcastIntent.action = BroadcastReceiver.ACTION_UPDATED_CHAPTER_LIST
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT)
    }

    override fun onCreate() {
        Log.v(M2kApplication.TAG, "Service ScanMangaIntentService created")
    }

    override fun onHandleWork(intent: Intent) {
        val scanManga = ScanManga()
        scanManga.performScan(this) {
            sendBroadcast(broadcastIntent)
            stopSelf()
        }
    }

    override fun onDestroy() {
        Log.v(M2kApplication.TAG, "Service ScanMangaIntentService destroyed")
    }
}