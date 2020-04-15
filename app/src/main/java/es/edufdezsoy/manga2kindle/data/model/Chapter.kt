package es.edufdezsoy.manga2kindle.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Chapter(
    /**
     * This id is not the identifier!
     * This id is the same as the server one
     */
    var id: Int?,
    val manga_id: Int,
    var lang_id: Int?,
    var volume: Int?,
    val chapter: Float,
    var title: String?,
    var file_path: String?,
    var checksum: String?,
    var style: String?,
    var split_mode: Int?,
    var delivered: Boolean,
    var error: Boolean,
    var reason: String?,
    var visible: Boolean
) {
    companion object {
        const val STATUS_DEFAULT = 0
        const val STATUS_ENQUEUE = 1
        const val STATUS_PROCESSING = 2
        const val STATUS_UPLOADING = 3
        const val STATUS_UPLOADED = 4
        const val STATUS_LOCAL_ERROR = 5
    }

    @PrimaryKey(autoGenerate = true)
    var identifier: Int = 0

    var status: Int = STATUS_DEFAULT
    var enqueue_date: Date? = null
    var upload_date: Date? = null

    override fun toString(): String {
        var text = ""

        if (volume != null)
            text += "Vol. $volume "

        var chapter = chapter.toString()
        if (chapter.isNotEmpty()) {
            if (chapter.indexOf(".") >= 0) {
                chapter = chapter.replace("0*$".toRegex(), "").replace("\\.$".toRegex(), "")
            }
        }

        text += "Ch. $chapter"

        if (title != null && title!!.isNotBlank())
            text += " - $title"

        return text
    }
}