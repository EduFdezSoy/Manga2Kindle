package es.edufdezsoy.manga2kindle.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Manga(
    @PrimaryKey val id: Int,
    val title: String,
    val author_id: String?
)