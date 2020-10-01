package es.edufdezsoy.manga2kindle.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Chapter(
    var manga_title: String,
    var author: String,
    var title: String?,
    var chapter: Int,
    var volume: Int?,
    var path: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var manga_id: Int? = null
    var remote_id: Int? = null
}