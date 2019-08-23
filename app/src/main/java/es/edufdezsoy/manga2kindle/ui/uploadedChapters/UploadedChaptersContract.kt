package es.edufdezsoy.manga2kindle.ui.uploadedChapters

import es.edufdezsoy.manga2kindle.data.model.Chapter

interface UploadedChaptersContract {
    interface Controller {
        fun loadChapters()
        fun openChapterDetails(chapter: Chapter)
        fun hideChapter(chapter: Chapter)
    }

    interface View {
        fun setChapters(chapters: List<Chapter>)
    }
}