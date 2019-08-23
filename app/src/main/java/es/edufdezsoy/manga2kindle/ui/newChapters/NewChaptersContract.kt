package es.edufdezsoy.manga2kindle.ui.newChapters

import es.edufdezsoy.manga2kindle.data.model.Chapter

interface NewChaptersContract {
    interface Controller {
        fun loadChapters()
        fun openChapterDetails(chapter: Chapter)
        fun hideChapter(chapter: Chapter)
    }

    interface View {
        fun setChapters(chapters: List<Chapter>)
    }
}