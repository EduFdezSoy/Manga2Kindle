package es.edufdezsoy.manga2kindle.data.model

import com.squareup.moshi.Json
import java.io.Serializable

data class UploadChapter(
    var id: String?,
    var mangaId: String?,
    var title: String?,
    var chapter: Float,
    var volume: Int?,
    var email: String?,
    @Json(name = "read_mode")
    var readMode: String?,
    @Json(name = "split_mode")
    var splitType: String?,
) : Serializable {
    constructor(chapterWithManga: ChapterWithManga) : this(chapterWithManga, null, null, null)
    constructor(chapterWithManga: ChapterWithManga, email: String?, readMode: String?, splitType: String?) : this(
        chapterWithManga.chapter.id,
        chapterWithManga.manga.uuid,
        chapterWithManga.chapter.title,
        chapterWithManga.chapter.chapter,
        chapterWithManga.chapter.volume,
        email,
        readMode,
        splitType
    )
}