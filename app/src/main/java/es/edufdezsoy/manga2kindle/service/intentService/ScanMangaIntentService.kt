package es.edufdezsoy.manga2kindle.service.intentService

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import es.edufdezsoy.manga2kindle.M2kApplication
import es.edufdezsoy.manga2kindle.service.ScanManga
import es.edufdezsoy.manga2kindle.service.util.BroadcastReceiver
import java.util.concurrent.atomic.AtomicBoolean

class ScanMangaIntentService : JobIntentService() {
    private val broadcastIntent = Intent()

    companion object {
        private val running = AtomicBoolean(false)

        fun enqueueWork(context: Context, intent: Intent) {
            if (!running.get())
                enqueueWork(context, ScanMangaIntentService::class.java, 1, intent)
        }
    }

    init {
        broadcastIntent.action = BroadcastReceiver.ACTION_SCAN_MANGA
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT)
    }

    override fun onHandleWork(intent: Intent) {
        Log.v(M2kApplication.TAG, "Service ScanMangaIntentService created")

        if (running.compareAndSet(false, true)) {
            val scanManga = ScanManga()
            scanManga.performScan(this) {
                running.set(false)
                sendBroadcast(broadcastIntent)
            }
        } else {
            Log.v(M2kApplication.TAG, "Service ScanMangaIntentService was already running")
        }
    }

    override fun onDestroy() {
        Log.v(M2kApplication.TAG, "Service ScanMangaIntentService destroyed")
    }
}