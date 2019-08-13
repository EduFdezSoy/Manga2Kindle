package es.edufdezsoy.manga2kindle.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import es.edufdezsoy.manga2kindle.data.model.Author

@Dao
interface AuthorDao {
    @Query("SELECT * FROM author")
    fun getAll(): List<Author>

    // TODO: the params in 'LIKE' maye be in between %, search how to edit the var before performing the query
    @Query("SELECT * FROM author WHERE name LIKE :search OR surname LIKE :search OR nickname LIKE :search")
    fun search(search: String): List<Author>

    @Insert
    fun insertAll(vararg authors: Author)

    @Delete
    fun delete(author: Author)
}