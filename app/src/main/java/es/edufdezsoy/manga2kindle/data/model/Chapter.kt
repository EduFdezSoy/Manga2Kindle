package es.edufdezsoy.manga2kindle.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Chapter(
    var title: String?,
    var chapter: Float,
    var volume: Int?,
    var path: String,
    val mangaId: Int,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var remoteId: String? = null

    fun chapterToString(): String {
        return if (chapter % 1.0 != 0.0)
            String.format("%s", chapter)
        else
            String.format("%.0f", chapter)
    }

}