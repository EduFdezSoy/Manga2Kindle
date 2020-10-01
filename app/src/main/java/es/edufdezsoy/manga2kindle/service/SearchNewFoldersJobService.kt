package es.edufdezsoy.manga2kindle.service

import android.app.job.JobParameters
import android.app.job.JobService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * TODO: Service incomplete
 * This service checks if some folders exists and adds them to the folder list (check folders for Tachiyomi, Neko, MangaPlus).
 * May check other apps from the play store  to add the to this service (surely there will be Tachiyomi clones).
 * This service may have a toggle button in the config to be disabled/enabled
 */
class SearchNewFoldersJobService : JobService(), CoroutineScope {
    //region vars and vals

    private val TAG = this::class.java.simpleName
    private var jobCancelled = false

    //endregion
    //region override methods

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    override fun onStartJob(params: JobParameters?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        TODO("Not yet implemented")
    }

    //endregion
    //region private methods


    //endregion
}