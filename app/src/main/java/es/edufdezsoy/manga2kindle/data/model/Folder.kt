package es.edufdezsoy.manga2kindle.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Folder(
    @PrimaryKey val id: Int,
    val name: String,
    val path: String
)