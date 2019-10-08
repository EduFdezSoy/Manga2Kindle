package es.edufdezsoy.manga2kindle.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import es.edufdezsoy.manga2kindle.M2kApplication

class ScanMangaJobService : JobService() {
    override fun onStartJob(params: JobParameters?): Boolean {
        Log.v(M2kApplication.TAG, "Service ScanMangaJobService created")

        val scanManga = ScanManga()
        scanManga.performScan(this) {
            jobFinished(params, true)
        }

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.v(M2kApplication.TAG, "Service ScanMangaJobService destroyed")

        return true
    }
}
