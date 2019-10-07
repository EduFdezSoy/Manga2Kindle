package es.edufdezsoy.manga2kindle.data.dao

import androidx.room.*
import es.edufdezsoy.manga2kindle.data.model.Manga

@Dao
interface MangaDao {
    /**
     * Get all the mangas in the database
     *
     * @return this list can be empty
     */
    @Query("SELECT * FROM manga")
    suspend fun getAll(): List<Manga>

    /**
     * Get a mangas by id
     *
     * @param manga_id the id of the manga we are requesting
     * @return the manga we requested
     */
    @Query("SELECT * FROM manga WHERE id = :manga_id")
    suspend fun getManga(manga_id: Int): Manga

    /**
     * Get a mangas by identifier
     *
     * @param manga_id the database unique identifier of the manga we are requesting
     * @return the manga we requested
     */
    @Query("SELECT * FROM manga WHERE identifier = :manga_id")
    suspend fun getMangaById(manga_id: Int): Manga

    /**
     * Get the last manga id inserted in the database
     *
     * @return the last id or null if the database is empty
     */
    @Query("SELECT * FROM manga ORDER BY id DESC LIMIT 1")
    suspend fun getLastManga(): Manga?

    /**
     * Search mangas that contains a certain string
     * TODO: the params in 'LIKE' maye be in between %, search how to edit the var before performing the query
     *
     * @param search the string we will search for in titles
     * @return this list can be empty
     */
    @Query("SELECT * FROM manga WHERE title LIKE :search")
    suspend fun search(search: String): List<Manga>

    /**
     * Insert one or many mangas
     */
    @Insert
    suspend fun insert(vararg manga: Manga)

    /**
     * Update a manga if the manga.identifier is in the database
     */
    @Update
    suspend fun update(manga: Manga)

    /**
     * Delete a manga
     */
    @Delete
    suspend fun delete(manga: Manga)
}