package es.edufdezsoy.manga2kindle.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import es.edufdezsoy.manga2kindle.M2kApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ScanManga : Service(), CoroutineScope {
    private val TAG = M2kApplication.TAG + "_ScanManga"
    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    override fun onCreate() {
        job = Job()
        Log.i(TAG, "Service created")
    }

    override fun onDestroy() {
        job.cancel()
        Log.i(TAG, "Service destroyed")
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "Service started")
        launch {
            var i = 0
            while (i < 3) {
                try {
                    Log.i(TAG, "Doing something")
                    Thread.sleep(10000)
                    i++

                } catch (e: Exception) {
                    Log.e(TAG, "Oops!", e)
                }
            }
        }
        return START_STICKY
    }
}
