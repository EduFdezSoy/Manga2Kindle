package es.edufdezsoy.manga2kindle.data.dao

import androidx.room.*
import es.edufdezsoy.manga2kindle.data.model.Chapter

@Dao
interface ChapterDao {
    /**
     * Get all the chapters in the database
     *
     * @return this list can be empty
     */
    @Query("SELECT * FROM chapter")
    suspend fun getAll(): List<Chapter>

    /**
     * Get a chapter by id
     *
     * @param chapter_id the local identifier from the chapter we are picking from the database
     * @return the chapter we requested
     */
    @Query("SELECT * FROM chapter WHERE identifier = :chapter_id")
    suspend fun getChapter(chapter_id: Int): Chapter

    /**
     * Get the non uploaded chapters from the database
     *
     * @return this list can be empty
     */
    @Query("SELECT * FROM chapter WHERE status = ${Chapter.STATUS_DEFAULT}")
    suspend fun getNoUploadedChapters(): List<Chapter>

    /**
     * Get the uploaded chapters from the database
     *
     * @return this list can be empty
     */
    @Query("SELECT * FROM chapter WHERE status != ${Chapter.STATUS_DEFAULT}")
    suspend fun getUploadedChapters(): List<Chapter>

    /**
     * Get a list with the hidden chapters from the database
     *
     * @return this list can be empty
     */
    @Query("SELECT * FROM chapter WHERE visible == 0")
    suspend fun getHiddenChapters(): List<Chapter>

    /**
     * Search authors that contains a certain string
     *
     * @param manga_id the id from the manga of the chapter we want
     * @param chapter the chapter number we are requesting
     * @return the chapter we requested or null
     */
    @Query("SELECT * FROM chapter WHERE manga_id = :manga_id AND chapter = :chapter")
    suspend fun search(manga_id: Int, chapter: Float): Chapter?

    /**
     * Insert a chapter
     */
    @Insert
    suspend fun insert(chapter: Chapter)

    /**
     * Update a chapter
     */
    @Update
    suspend fun update(chapter: Chapter)

    /**
     * "Delete" a chapter, it only hide it as we cant remove them, if we do that the service would pick it again
     */
    @Query("UPDATE chapter SET visible = 0 WHERE identifier = :chapter_id")
    suspend fun hide(chapter_id: Int)

    @Delete
    suspend fun delete(chapter: Chapter)

    /**
     * Delete the chapters that hasn't been sent to the server
     */
    @Query("DELETE FROM chapter WHERE status != ${Chapter.STATUS_UPLOADED}")
    suspend fun clearNotSended()
}