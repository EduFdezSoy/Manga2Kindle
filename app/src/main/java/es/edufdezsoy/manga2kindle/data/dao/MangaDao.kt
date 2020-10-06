package es.edufdezsoy.manga2kindle.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import es.edufdezsoy.manga2kindle.data.model.ChapterWithManga
import es.edufdezsoy.manga2kindle.data.model.Manga

@Dao
interface MangaDao {
    @Insert
    suspend fun insert(manga: Manga)

    @Update
    suspend fun update(manga: Manga)

    @Delete
    suspend fun delete(manga: Manga)

    @Query("DELETE FROM Manga")
    suspend fun deleteAll()

    @Transaction
    @Query("SELECT * FROM Manga")
    fun getAllNotes(): LiveData<List<ChapterWithManga>>
}