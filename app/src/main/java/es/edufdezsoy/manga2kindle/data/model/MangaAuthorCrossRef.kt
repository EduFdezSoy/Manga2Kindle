package es.edufdezsoy.manga2kindle.data.model

import androidx.room.Entity

@Entity(primaryKeys = ["mangaId", "authorId"])
data class MangaAuthorCrossRef(
    val mangaId: Int,
    val authorId: Int
)