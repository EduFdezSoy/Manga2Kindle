package es.edufdezsoy.manga2kindle

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import es.edufdezsoy.manga2kindle.utils.Log
import es.edufdezsoy.manga2kindle.service.ScanFoldersForMangaJobService
import es.edufdezsoy.manga2kindle.service.ScanRemovedChaptersService

class Application : Application() {
    private val TAG = this::class.java.simpleName

    companion object {
        const val CHANNEL_ID = "ServiceChannel"
        const val SCAN_FOR_MANGA_ID = 1
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
        startScheduledService()
        startOneTimeServices()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Example Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun startScheduledService() {
        val cn = ComponentName(applicationContext, ScanFoldersForMangaJobService::class.java)
        val ji = JobInfo.Builder(SCAN_FOR_MANGA_ID, cn)
            .setRequiresCharging(false)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPersisted(true)
            .setPeriodic(15 * 60 * 1000) // 15 mins, the lower valid value

        val scheduler = applicationContext.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        val resultCode = scheduler.schedule(ji.build())
        if (resultCode == JobScheduler.RESULT_SUCCESS)
            Log.d(TAG, "startScheduledService: Job Scheduled")
        else
            Log.d(TAG, "startScheduledService: Job Scheduling failed")
    }

    private fun startOneTimeServices() {
        val serviceIntent = Intent(applicationContext, ScanRemovedChaptersService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            applicationContext.startForegroundService(serviceIntent)
        } else {
            applicationContext.startService(serviceIntent)
        }
    }
}