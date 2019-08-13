package es.edufdezsoy.manga2kindle.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Chapter(
    @PrimaryKey val id: Int,
    val manga_id: Int,
    val lang_id: Int,
    val volume: Int?,
    val chapter: Float,
    val title: String?,
    val file_path: String?,
    val checksum: String
)