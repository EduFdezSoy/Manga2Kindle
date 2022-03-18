package es.edufdezsoy.manga2kindle.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.graphics.Bitmap
import android.util.Log
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.Compression
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ExampleJobService : JobService(), CoroutineScope {
    //region vars and vals
    private val TAG = this::class.java.simpleName
    private var jobCancelled = false

    //endregion
    //region override methods

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(TAG, "onStartJob: job started")

        doBackgroundWork(params)

        return true // true means we are doing things in the background
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d(TAG, "onStopJob: Job Cancelled before completion")
        jobCancelled = true
        return true // true if we need to run it again (if something fails, for example)
    }

    //endregion
    //region private methods

    private fun doBackgroundWork(params: JobParameters?) {
        launch {
            for (i in 1..10) {
                Log.d(TAG, "doBackgroundWork: running $i")

                if (jobCancelled)
                    return@launch

                Thread.sleep(1000)
            }


            Log.d(TAG, "doBackgroundWork: Job Finished")
            jobFinished(
                params,
                false
            ) // true if we need to run it again (if something fails, for example)
        }
    }

    //endregion
}