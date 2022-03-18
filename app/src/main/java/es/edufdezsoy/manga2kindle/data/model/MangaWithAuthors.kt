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
    var authors: List<Author>
) {
    fun authorsToString(): String {
        var names = ""

        authors.forEach {
            if (names.isNotBlank())
                names += ", "
            names += it.name
        }

        return names
    }
}