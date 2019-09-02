package es.edufdezsoy.manga2kindle.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import es.edufdezsoy.manga2kindle.M2kApplication
import es.edufdezsoy.manga2kindle.data.M2kDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.net.URI
import kotlin.coroutines.CoroutineContext

class ScanManga : Service(), CoroutineScope {
    private val TAG = M2kApplication.TAG + "_ScanManga"
    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

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
            val folders = M2kDatabase(this@ScanManga).FolderDao().getAll()
            folders.forEach {
                val f = File(it.path).list()
                Log.d(TAG, it.path)

                if (!f.isNullOrEmpty())
                    f.forEach { s ->
                            Log.d(TAG, "Inside:")

                        if (s.isNotEmpty())
                            Log.d(TAG, s)
                    }
            }
        }
        return START_STICKY
    }
}
