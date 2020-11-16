package es.edufdezsoy.manga2kindle.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class ChapterWithManga(
    @Embedded val manga: MangaWithAuthors,
    @Relation(
        parentColumn = "mangaId",
        entityColumn = "mangaId"
    )
    val chapter: Chapter
)