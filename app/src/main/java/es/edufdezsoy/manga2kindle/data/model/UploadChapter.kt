package es.edufdezsoy.manga2kindle.data.model

import java.io.Serializable

data class UploadChapter(
    var manga: UploadManga,
    var title: String?,
    var chapter: Float,
    var volume: Int?,
    var pages: Int?,
    var email: String?,
    var readMode: String?,
    var splitType: Int?,
    var path: String?
) : Serializable {
    constructor(chapterWithManga: ChapterWithManga) : this(chapterWithManga, null, null)
    constructor(chapterWithManga: ChapterWithManga, readMode: String?, splitType: Int?) : this(
        UploadManga(chapterWithManga.manga),
        chapterWithManga.chapter.title,
        chapterWithManga.chapter.chapter,
        chapterWithManga.chapter.volume,
        null,
        null,
        readMode,
        splitType,
        chapterWithManga.chapter.path
    )
}