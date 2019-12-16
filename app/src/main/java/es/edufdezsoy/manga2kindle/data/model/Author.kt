package es.edufdezsoy.manga2kindle.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Author(
    /**
     * The id mus't be the same in the server
     */
    @PrimaryKey val id: Int,
    val name: String?,
    val surname: String?,
    val nickname: String?
) {
    override fun toString(): String {
        var text = ""

        if (!surname.isNullOrEmpty() || !name.isNullOrEmpty()) {
            if (!surname.isNullOrEmpty())
                text += "$surname "
            if (!name.isNullOrEmpty())
                text += "$name "
            if (!nickname.isNullOrEmpty())
                text += "(AKA $nickname)"
        } else {
            if (!nickname.isNullOrEmpty())
                text += nickname
        }

        return text
    }
}