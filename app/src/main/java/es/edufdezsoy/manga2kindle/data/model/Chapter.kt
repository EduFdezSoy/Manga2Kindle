package es.edufdezsoy.manga2kindle.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Chapter(
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
    var sended: Boolean =  false

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