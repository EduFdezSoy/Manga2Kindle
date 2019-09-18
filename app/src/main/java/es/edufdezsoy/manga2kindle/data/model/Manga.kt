package es.edufdezsoy.manga2kindle.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Manga(
    var id: Int?,
    val title: String,
    val author_id: Int?
) {
    @PrimaryKey(autoGenerate = true)
    var identifier: Int = 0
    var synchronized: Boolean = false
}