package es.edufdezsoy.manga2kindle.ui.newChapters

import es.edufdezsoy.manga2kindle.data.model.viewObject.NewChapter

interface NewChaptersContract {
    interface Controller {
        fun loadChapters()
        fun reloadChapters()
        fun openChapterDetails(chapter: NewChapter)
        fun hideChapter(chapter: NewChapter)
    }

    interface View {
        fun setChapters(chapters: List<NewChapter>)
    }
}