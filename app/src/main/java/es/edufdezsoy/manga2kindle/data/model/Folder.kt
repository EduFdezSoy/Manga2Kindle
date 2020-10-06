package es.edufdezsoy.manga2kindle.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Folder(
    var name: String,
    var path: String,
    var color: String
) {
    @PrimaryKey(autoGenerate = true)
    var folderId: Int = 0
}