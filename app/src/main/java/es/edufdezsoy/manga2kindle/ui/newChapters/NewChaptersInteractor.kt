package es.edufdezsoy.manga2kindle.ui.newChapters

import es.edufdezsoy.manga2kindle.data.M2kDatabase
import es.edufdezsoy.manga2kindle.data.model.Chapter

class NewChaptersInteractor(val controller: Controller, val database: M2kDatabase) {
    interface Controller {
        fun setNewChapters(chapters: List<Chapter>)
    }

    suspend fun loadChapters() {
        val chapters = database.ChapterDao().getAll()
        // TODO: FILTER UPLOADED ONES, CREATE A NEW QUERY
        controller.setNewChapters(chapters)
    }
}