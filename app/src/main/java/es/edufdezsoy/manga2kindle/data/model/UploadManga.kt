package es.edufdezsoy.manga2kindle.data.model

import java.io.Serializable

data class UploadManga(
    var id: Int?,
    var title: String,
    var uuid: String?,
    var author: ArrayList<Author>
) : Serializable {
    constructor(manga: MangaWithAuthors) : this(
        manga.manga.remoteId,
        manga.manga.title,
        manga.manga.uuid,
        manga.authors as ArrayList<Author>
    )
}