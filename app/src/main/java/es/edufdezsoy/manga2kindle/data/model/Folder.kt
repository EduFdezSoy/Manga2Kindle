package es.edufdezsoy.manga2kindle.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Folder(
    @PrimaryKey var id: Int,
    var name: String,
    var path: String
)