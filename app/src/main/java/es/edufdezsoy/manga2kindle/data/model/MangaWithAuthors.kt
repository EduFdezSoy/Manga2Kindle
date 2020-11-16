package es.edufdezsoy.manga2kindle.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class MangaWithAuthors(
    @Embedded val manga: Manga,
    @Relation(
        parentColumn = "mangaId",
        entityColumn = "authorId",
        associateBy = Junction(MangaAuthorCrossRef::class)
    )
    val authors: List<Author>
)