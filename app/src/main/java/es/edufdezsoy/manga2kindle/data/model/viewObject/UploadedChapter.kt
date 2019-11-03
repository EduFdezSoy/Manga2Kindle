package es.edufdezsoy.manga2kindle.data.model.viewObject

import java.util.*

class UploadedChapter(
    val server_id: Int,
    val local_id: Int,

    val chapter: String,

    val manga_id: Int,
    val manga_title: String,

    val author_id: Int?,
    val author: String,

    val status_id: Int,
    val status: String,
    val status_color: Int,
    val reason: String,

    val enqueue_date: Date?,
    val upload_date: Date?
) {
    override fun equals(other: Any?): Boolean {
        if (other is UploadedChapter)
            if (server_id == other.server_id)
                if (local_id == other.local_id)
                    if (status_id == other.status_id)
                        return true
        return false
    }

    override fun hashCode(): Int {
        return server_id.hashCode() + local_id.hashCode() + status_id.hashCode()
    }

    class Sort : Comparator<UploadedChapter> {
        override fun compare(p0: UploadedChapter, p1: UploadedChapter): Int {
            if (p0.upload_date == null && p1.upload_date == null)
                if (p0.enqueue_date == null && p1.enqueue_date == null)
                    return p0.local_id.compareTo(p1.local_id)
                else
                    if (p0.enqueue_date == null)
                        return 1
                    else if (p1.enqueue_date == null)
                        return -1
                    else
                        return p0.enqueue_date.compareTo(p1.enqueue_date)
            else
                if (p0.upload_date == null)
                    return 1
                else if (p1.upload_date == null)
                    return -1
                else
                    return p0.upload_date.compareTo(p1.upload_date)
        }
    }
}