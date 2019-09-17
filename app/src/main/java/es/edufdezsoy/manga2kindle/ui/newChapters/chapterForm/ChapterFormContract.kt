package es.edufdezsoy.manga2kindle.ui.newChapters.chapterForm

import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.data.model.Manga

interface ChapterFormContract {
    interface Controller {
        fun saveData(chapter: Chapter, manga: Manga)
        fun sendChapter(chapter: Chapter, manga: Manga, mail: String)
    }

    interface View {
        fun setView(chapter: Chapter, manga: Manga, mail: String?)
    }
}