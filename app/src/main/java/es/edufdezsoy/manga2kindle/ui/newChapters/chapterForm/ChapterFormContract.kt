package es.edufdezsoy.manga2kindle.ui.newChapters.chapterForm

import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.data.model.Manga

interface ChapterFormContract {
    interface Controller {
        fun saveData(chapter: Chapter, manga: Manga, mail: String?)
        fun sendChapter(chapter: Chapter, manga: Manga, mail: String)
        fun cancelEdit()
    }

    interface View {
        fun setChapter(chapter: Chapter)
        fun setManga(manga: Manga)
        fun setMail(mail: String)
    }
}