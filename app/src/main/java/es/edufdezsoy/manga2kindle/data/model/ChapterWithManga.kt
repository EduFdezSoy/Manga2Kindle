package es.edufdezsoy.manga2kindle.data.model

import java.io.Serializable

data class ChapterWithManga(
    var chapter: Chapter,
    var manga: Manga
) : Serializable
