package es.edufdezsoy.manga2kindle.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Chapter(
    var title: String?,
    var chapter: Float,
    var volume: Int?,
    var path: String,
    val mangaId: Int,
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var rowid = 0
    var id: String? = null
    var status: String = ""

    fun chapterToString(): String {
        return if (chapter % 1.0 != 0.0)
            String.format("%s", chapter)
        else
            String.format("%.0f", chapter)
    }
}