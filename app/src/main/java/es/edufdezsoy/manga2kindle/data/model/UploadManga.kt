package es.edufdezsoy.manga2kindle.data.model

import java.io.Serializable

data class UploadManga(
    var title: String,
    var author: String?,
    var id: Int
) : Serializable {
    constructor(manga: Manga) : this(
        manga.title,
        manga.author,
        manga.mangaId
    )
}