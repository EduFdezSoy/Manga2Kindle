package es.edufdezsoy.manga2kindle.data.model.viewObject

class NewChapter(
    val local_id: Int,
    val chapter: String,

    val manga_id: Int?,
    val manga_local_id: Int,
    val manga_title: String,

    val author_id: Int?,
    val author: String
)