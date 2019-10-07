package es.edufdezsoy.manga2kindle.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Manga(
    /**
     * This id is not the identifier!
     * This id is the same as the server one
     */
    var id: Int?,
    val title: String,
    val author_id: Int?
) {
    @PrimaryKey(autoGenerate = true)
    var identifier: Int = 0
    var synchronized: Boolean = false
}