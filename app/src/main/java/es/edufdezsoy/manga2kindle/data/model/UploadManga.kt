package es.edufdezsoy.manga2kindle.data.model

data class UploadManga(
    var id: Int?,
    var title: String,
    var uuid: String?,
    var author: List<Author>
) {
    constructor(manga: MangaWithAuthors) : this(
        manga.manga.remoteId,
        manga.manga.title,
        manga.manga.uuid,
        manga.authors
    )
}