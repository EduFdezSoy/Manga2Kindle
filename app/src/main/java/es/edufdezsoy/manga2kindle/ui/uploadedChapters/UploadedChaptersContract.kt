package es.edufdezsoy.manga2kindle.ui.uploadedChapters

import es.edufdezsoy.manga2kindle.data.model.viewObject.UploadedChapter

interface UploadedChaptersContract {
    interface Controller {
        fun loadChapters()
        fun reloadChapters()
        fun openChapterDetails(chapter: UploadedChapter)
        fun hideChapter(chapter: UploadedChapter)
    }

    interface View {
        fun setChapters(chapters: List<UploadedChapter>)
    }
}