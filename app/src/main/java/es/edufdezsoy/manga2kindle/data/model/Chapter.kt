package es.edufdezsoy.manga2kindle.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

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
    var delivered: Boolean,
    var error: Boolean,
    var reason: String?,
    var visible: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    var identifier: Int = 0

    /**
     * 0 = default
     * 1 = processing
     * 2 = uploading
     * 3 = uploaded
     * 4 = local error
     */
    var status: Int = 0

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

        if (title != null)
            text += " - $title"

        return text
    }
}