package es.edufdezsoy.manga2kindle.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Language(
    /**
     * The id mus't be the same in the server
     */
    @PrimaryKey val id: Int,
    val code: String,
    val name: String
)