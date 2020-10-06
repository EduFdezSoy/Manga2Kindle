package es.edufdezsoy.manga2kindle.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Chapter(
    var title: String?,
    var chapter: Int,
    var volume: Int?,
    var path: String,
    val mangaId: Int,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var remoteId: String? = null
}