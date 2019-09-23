package es.edufdezsoy.manga2kindle.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Chapter(
    var id: Int?,
    val manga_id: Int,
    var lang_id: Int?,
    val volume: Int?,
    val chapter: Float,
    var title: String?,
    var file_path: String?,
    var checksum: String?,
    var delivered: Boolean,
    var error: Boolean,
    var reason: String?,
    var visible: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    val identifier: Int = 0
    var sended: Boolean =  false
}