package es.edufdezsoy.manga2kindle.ui.uploaded

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import es.edufdezsoy.manga2kindle.data.model.Status
import es.edufdezsoy.manga2kindle.data.repository.ChapterRepository
import es.edufdezsoy.manga2kindle.data.repository.StatusRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class StatusViewModel(application: Application) : AndroidViewModel(application) {
    private val statusRemoteDataSource = StatusRemoteDataSource(application)
    private val chapterRepository = ChapterRepository(application)

    init {
        viewModelScope.launch {
            statusRemoteDataSource.statuses.collect {
                it.forEach { uploadChapter ->
                    launch(Dispatchers.IO) {
                        val chapter = chapterRepository.getByRemoteId(uploadChapter.id!!)
//                    if (chapter!!.status != uploadChapter.status) {
//                        chapter.status = uploadChapter.status
//                        chapterRepository.update(chapter)
//
//                        // this will clear the remote id in the db and the api
//                        if (it.status >= Status.DONE) {
//                            statusRemoteDataSource.freeChapterStatus(uploadChapter.id!!)
//                        }
//                    }
                    }
                }
            }
        }
    }
}