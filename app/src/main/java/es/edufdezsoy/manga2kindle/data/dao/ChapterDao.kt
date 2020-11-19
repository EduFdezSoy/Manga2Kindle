package es.edufdezsoy.manga2kindle.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.data.model.ChapterWithManga

@Dao
interface ChapterDao {
    @Insert
    suspend fun insert(chapter: Chapter)

    @Update
    suspend fun update(chapter: Chapter)

    @Delete
    suspend fun delete(chapter: Chapter)

    @Query("DELETE FROM Chapter")
    suspend fun deleteAllNotes()

    @Transaction
    @Query("SELECT * FROM Manga")
    fun getAllChapters(): LiveData<List<ChapterWithManga>>

    @Query("SELECT * FROM Chapter WHERE mangaId = :mangaId AND chapter = :chapterNum")
    suspend fun search(mangaId: Int, chapterNum: Float): Chapter?
}