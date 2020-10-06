package es.edufdezsoy.manga2kindle.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Manga(
    @ColumnInfo(name = "manga_title")
    var title: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var remoteId: Int? = null
    var uuid: String? = null
    var author: String? = null
}