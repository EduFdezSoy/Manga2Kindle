package es.edufdezsoy.manga2kindle.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Manga(
    var title: String
) {
    @PrimaryKey(autoGenerate = true)
    var mangaId: Int = 0
    var remoteId: Int? = null
    var uuid: String? = null
}