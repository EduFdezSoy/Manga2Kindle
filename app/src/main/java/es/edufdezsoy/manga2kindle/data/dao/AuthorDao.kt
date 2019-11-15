package es.edufdezsoy.manga2kindle.data.dao

import androidx.room.*
import es.edufdezsoy.manga2kindle.data.model.Author

@Dao
interface AuthorDao {
    /**
     * Get all the authors in the database
     *
     * @return this list can be empty
     */
    @Query("SELECT * FROM author")
    suspend fun getAll(): List<Author>

    /**
     * Get an author by id
     *
     * @param author_id the id from the author we are picking from the database
     * @return the author we requested or null
     */
    @Query("SELECT * FROM author WHERE id = :author_id")
    suspend fun getAuthor(author_id: Int): Author?

    /**
     * Search authors that contains a certain string
     *
     * @param search the string we will search for in names, surnames or nicknames
     * @return this list can be empty
     */
    @Query("SELECT * FROM author WHERE instr(UPPER(name), UPPER(:search)) OR instr(UPPER(surname), UPPER(:search)) OR instr(UPPER(nickname), UPPER(:search))")
    suspend fun search(search: String): List<Author>

    /**
     * Insert one or many authors, if the author exists it will replace it
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg authors: Author)

    /**
     * Update an author
     */
    @Update
    suspend fun update(author: Author)

    /**
     * Delete an author
     */
    @Delete
    suspend fun delete(author: Author)
}