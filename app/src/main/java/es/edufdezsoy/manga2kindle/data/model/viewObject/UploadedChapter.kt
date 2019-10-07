package es.edufdezsoy.manga2kindle.data.model.viewObject

class UploadedChapter(
    val server_id: Int,
    val local_id: Int,

    val chapter: String,

    val manga_id: Int,
    val manga_title: String,

    val author_id: Int?,
    val author: String,

    val status: String,
    val status_color: Int,
    val reason: String
) {
    override fun equals(other: Any?): Boolean {
        if (other is UploadedChapter)
            if (server_id == other.server_id)
                if (local_id == other.local_id)
                    if (status == other.status)
                        return true
        return false
    }

    override fun hashCode(): Int {
        return server_id.hashCode() + local_id.hashCode() + status.hashCode()
    }
}