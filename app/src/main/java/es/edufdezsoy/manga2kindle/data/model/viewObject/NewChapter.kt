package es.edufdezsoy.manga2kindle.data.model.viewObject

class NewChapter(
    val local_id: Int,
    val chapter: String,

    val manga_id: Int?,
    val manga_local_id: Int,
    val manga_title: String,

    val author_id: Int?,
    val author: String
) {
    override fun equals(other: Any?): Boolean {
        if (other is NewChapter)
            if (local_id == other.local_id)
                if (manga_local_id == other.manga_local_id)
                    if (manga_id == other.manga_id)
                        if (author_id == other.author_id)
                            if (chapter == other.chapter)
                                if (manga_title == other.manga_title)
                                    if (author == other.author)
                                        return true
        return false
    }

    override fun hashCode(): Int {
        return local_id.hashCode() + manga_local_id.hashCode() + author_id.hashCode()
    }
}