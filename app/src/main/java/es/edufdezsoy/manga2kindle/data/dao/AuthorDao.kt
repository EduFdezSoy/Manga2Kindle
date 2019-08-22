package es.edufdezsoy.manga2kindle.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import es.edufdezsoy.manga2kindle.data.model.Author

@Dao
interface AuthorDao {
    @Query("SELECT * FROM author")
    suspend fun getAll(): List<Author>

    @Query("SELECT * FROM author WHERE id = :author_id")
    suspend fun getAuthor(author_id: Int): Author

    // TODO: the params in 'LIKE' maye be in between %, search how to edit the var before performing the query
    @Query("SELECT * FROM author WHERE name LIKE :search OR surname LIKE :search OR nickname LIKE :search")
    suspend fun search(search: String): List<Author>

    @Insert
    suspend fun insert(vararg authors: Author)

    @Delete
    suspend fun delete(author: Author)
}