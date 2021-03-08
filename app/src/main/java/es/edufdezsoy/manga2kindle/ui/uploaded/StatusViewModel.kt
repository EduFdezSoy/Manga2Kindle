package es.edufdezsoy.manga2kindle.ui.uploaded

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import es.edufdezsoy.manga2kindle.data.repository.ChapterRepository
import es.edufdezsoy.manga2kindle.data.repository.StatusRemoteDataSource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class StatusViewModel(application: Application) : AndroidViewModel(application) {
    private val statusRemoteDataSource = StatusRemoteDataSource(application)
    private val chapterRepository = ChapterRepository(application)

    init {
        viewModelScope.launch {
            statusRemoteDataSource.statuses.collect {
                it.forEach {
                    val chapter = chapterRepository.getByRemoteId(it.id)
                    if (chapter!!.status != it.status) {
                        chapter.status = it.status
                        chapterRepository.update(chapter)
                    }
                }
            }
        }
    }
}