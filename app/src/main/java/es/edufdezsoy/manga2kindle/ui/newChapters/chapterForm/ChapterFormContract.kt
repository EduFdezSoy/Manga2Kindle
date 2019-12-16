package es.edufdezsoy.manga2kindle.ui.newChapters.chapterForm

import es.edufdezsoy.manga2kindle.data.model.Author
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.data.model.Manga

interface ChapterFormContract {
    interface Controller {
        fun saveData(chapter: Chapter, manga: Manga)
        fun actionSaveData()
        fun sendChapter(chapter: Chapter)
        fun searchAuthors(str: String)
        fun openAuthorForm()
        fun cancelEdit()
    }

    interface View {
        fun saveData()
        fun setChapter(chapter: Chapter)
        fun setManga(manga: Manga)
        fun setAuthor(author: Author)
        fun setAuthors(authors: List<Author>)
    }
}