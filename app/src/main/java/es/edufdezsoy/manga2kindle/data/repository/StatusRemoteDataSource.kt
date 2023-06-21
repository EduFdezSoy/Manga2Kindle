package es.edufdezsoy.manga2kindle.data.repository

import android.app.Application
import es.edufdezsoy.manga2kindle.data.model.Status
import es.edufdezsoy.manga2kindle.data.model.UploadChapter
import es.edufdezsoy.manga2kindle.network.ApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StatusRemoteDataSource(
    application: Application,
    private val refreshIntervalMs: Long = 3000
) {
    private val apiService = ApiService.getInstance(application.applicationContext)
    private val chapterRepository = ChapterRepository(application)

    val statuses: Flow<List<UploadChapter>> = flow {
        while (true) {
            chapterRepository.getAllChapters().collect {
                val chapters = it
                    .filterNot { item -> item.status.isBlank() }
                    .filterNot { item -> item.status == Status.DONE }

                val statuses: ArrayList<UploadChapter> = arrayListOf()
                for (ch in chapters) {
                    if (ch.id != null) {
                        statuses.add(apiService.getChapterStatus(ch.id!!))
                    }
                }

                emit(statuses)
                delay(refreshIntervalMs)
            }
        }
    }

    suspend fun freeChapterStatus(id: String) {
        apiService.deleteChapterStatus(id)
        val chapter = chapterRepository.getByRemoteId(id)
        chapter!!.id = null
        chapterRepository.update(chapter)
    }
}