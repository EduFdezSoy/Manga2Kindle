package es.edufdezsoy.manga2kindle.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Author(
    var name: String
) {
    @PrimaryKey(autoGenerate = true)
    var authorId: Int = 0
    var remoteId: Int? = null
}