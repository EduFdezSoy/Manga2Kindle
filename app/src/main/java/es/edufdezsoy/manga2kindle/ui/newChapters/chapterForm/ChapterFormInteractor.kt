package es.edufdezsoy.manga2kindle.ui.newChapters.chapterForm

import es.edufdezsoy.manga2kindle.data.M2kDatabase
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.data.model.Manga

class ChapterFormInteractor(val controller: Controller, val database: M2kDatabase) {
    interface Controller {
        fun setManga(manga: Manga)
        fun setMail(mail: String?)
        fun done()
    }

    suspend fun saveChapter(chapter: Chapter) {
        database.ChapterDao().update(chapter).also { controller.done() }
    }

    suspend fun getManga(id: Int) {
        database.MangaDao().getMangaById(id).also { controller.setManga(it) }
    }

    suspend fun saveManga(manga: Manga) {
        database.MangaDao().update(manga).also { controller.done() }
    }

    suspend fun getMail() {
        // TODO(get mail from shared preferences)
        controller.setMail(null)
    }

    suspend fun saveMail(mail: String) {
        TODO("save mail into shared preferences")
    }
}