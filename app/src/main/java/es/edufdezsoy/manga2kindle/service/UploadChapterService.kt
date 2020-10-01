package es.edufdezsoy.manga2kindle.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * TODO: Service  incomplete
 * This service may be launch when chapters needs to be uploaded, uses a notification as it needs to
 * be launched as fast as possible and needs to keep working even if the user closes the app to
 * deliver the chapters.
 *
 * In order to reduce network problems to a minimum chapters wont be uploaded as a zip, instead we
 * will upload pages one by one. If one fails we only retry that one.
 *
 * By doing it one by one we can also stop the process at any time and keep track of the % completed
 *
 * We will want the images already compressed and scaled
 */
class UploadChapterService : Service(), CoroutineScope {
    //region vars and vals
    private val TAG = this::class.java.simpleName

    //endregion
    //region override methods

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    //endregion
}