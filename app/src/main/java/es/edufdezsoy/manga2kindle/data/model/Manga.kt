package es.edufdezsoy.manga2kindle.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import java.io.Serializable

@Entity
data class Manga(
    var title: String
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var mangaId: Int = 0

    @Json(name = "id")
    var uuid: String? = null
    var author: String? = null

    // extra data from the server
    @Ignore
    var collectionId: String? = null
    @Ignore
    var collectionName: String? = null
    @Ignore
    var created: String? = null
    @Ignore
    var updated: String? = null
}