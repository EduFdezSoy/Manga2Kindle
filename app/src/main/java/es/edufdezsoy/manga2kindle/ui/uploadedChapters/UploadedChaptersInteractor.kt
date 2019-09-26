package es.edufdezsoy.manga2kindle.ui.uploadedChapters

import es.edufdezsoy.manga2kindle.data.M2kDatabase
import es.edufdezsoy.manga2kindle.data.model.Chapter

class UploadedChaptersInteractor(val controller: Controller, val database: M2kDatabase) {
    interface Controller {
        fun setNewChapters(chapters: List<Chapter>)
    }

    suspend fun loadChapters() {
        database.ChapterDao().getUploadedChapters().also {
            controller.setNewChapters(it)
        }
    }
}