package es.edufdezsoy.manga2kindle.service

import android.Manifest
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.documentfile.provider.DocumentFile
import es.edufdezsoy.manga2kindle.Application.Companion.CHANNEL_ID
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.data.model.Folder
import es.edufdezsoy.manga2kindle.data.repository.ChapterRepository
import es.edufdezsoy.manga2kindle.data.repository.FolderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class ScanRemovedChaptersService : Service(), CoroutineScope {
    val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setContentTitle("Check Chapters Service")
            setContentText("checking...")
            setProgress(0, 0, true)
            setSmallIcon(R.drawable.ic_tpose) // TODO: change icon
            priority = NotificationCompat.PRIORITY_LOW
        }

        val notificationManager = NotificationManagerCompat.from(this)
        startForeground(1, notification.build()) // TODO: move id to a resource

        launch {
            val chRepo = ChapterRepository(application)
            val folderRepo = FolderRepository(application)
            val chapters = chRepo.getStaticAllChapters()
            val folders = folderRepo.getStaticActiveFolders().value
            val PROGRESS_MAX = chapters.size

            if (ActivityCompat.checkSelfPermission(
                    baseContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Log.e("TODO", "POST_NOTIFICATIONS not granted")
            }

            notificationManager.notify(1, notification.build())

            chapters.forEachIndexed { i: Int, chapter: Chapter ->
                notification.setProgress(PROGRESS_MAX, i + 1, false)
                notificationManager.notify(1, notification.build())

                val docFile = DocumentFile.fromSingleUri(
                    this@ScanRemovedChaptersService,
                    Uri.parse(chapter.path)
                )

                if (docFile == null || !docFile.exists()) {
                    chRepo.delete(chapter)
                }
            }

            stopSelf()
        }

        return START_NOT_STICKY
    }

}