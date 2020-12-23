package es.edufdezsoy.manga2kindle.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Author(
    var name: String
): Serializable {
    @PrimaryKey(autoGenerate = true)
    var authorId: Int = 0
    var remoteId: Int? = null
}