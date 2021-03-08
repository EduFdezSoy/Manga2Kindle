package es.edufdezsoy.manga2kindle.data.repository

import android.app.Application
import es.edufdezsoy.manga2kindle.data.model.Status
import es.edufdezsoy.manga2kindle.network.ApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StatusRemoteDataSource(
    private val application: Application,
    private val refreshIntervalMs: Long = 3000
) {
    private val apiService = ApiService.getInstance(application.applicationContext)
    private val chapterRepository = ChapterRepository(application)

    val statuses: Flow<List<Status>> = flow {
        while (true) {
            val chapters = chapterRepository.getStaticAllChapters()
                .filterNot { item -> item.status == 0 }
                .filterNot { item -> item.status >= Status.DONE }

            val statuses: ArrayList<Status> = arrayListOf()
            for (ch in chapters) {
                statuses.add(apiService.getChapterStatus(ch.remoteId!!))
            }

            emit(statuses)
            delay(refreshIntervalMs)
        }
    }

    suspend fun freeChapterStatus(id: Int) {
        apiService.deleteChapterStatus(id)
        val chapter = chapterRepository.getByRemoteId(id)
        chapter!!.remoteId = null
        chapterRepository.update(chapter)
    }
}