package es.edufdezsoy.manga2kindle.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import es.edufdezsoy.manga2kindle.Application.Companion.CHANNEL_ID
import es.edufdezsoy.manga2kindle.MainActivity
import es.edufdezsoy.manga2kindle.R


class ExampleService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val input = intent?.getStringExtra("inputExtra")

        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.putExtra("fragment", 2)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Example Service")
            .setContentText(input)
            .setSmallIcon(R.drawable.ic_tpose)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)

//        stopSelf()
        // DOES NOT START A NEW THREAD BY DEFAULT (works in the ui-thread)

        return START_NOT_STICKY
    }

}