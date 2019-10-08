package es.edufdezsoy.manga2kindle.service.util

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import es.edufdezsoy.manga2kindle.service.ScanMangaJobService

class ServiceScheduler {
    fun sheduleScanManga(context: Context) {
        val serviceComponent = ComponentName(context, ScanMangaJobService::class.java)
        val jobBuilder = JobInfo.Builder(0, serviceComponent)

        // TODO: would be fine to put those times in the settings
//        jobBuilder.setMinimumLatency(10 * 60 * 1000) // 10 minutes
//        jobBuilder.setOverrideDeadline(30 * 60 * 1000) // 30 minutes

        jobBuilder.setMinimumLatency(1 * 60 * 1000) // 1 minutes
        jobBuilder.setOverrideDeadline(5 * 60 * 1000) // 5 minutes

        val jobSheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobSheduler.schedule(jobBuilder.build())
    }
}