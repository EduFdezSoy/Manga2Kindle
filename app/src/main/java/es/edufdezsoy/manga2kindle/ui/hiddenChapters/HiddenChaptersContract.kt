package es.edufdezsoy.manga2kindle.ui.hiddenChapters

import es.edufdezsoy.manga2kindle.data.model.viewObject.HiddenChapter

interface HiddenChaptersContract {
    interface Controller {
        fun loadChapters()
        fun openChapterDetails(chapter: HiddenChapter)
        fun showChapter(chapter: HiddenChapter)
        fun hideChapter(chapter: HiddenChapter)
    }

    interface View {
        fun setChapters(chapters: List<HiddenChapter>)
    }
}