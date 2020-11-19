package es.edufdezsoy.manga2kindle.data.dao

import androidx.room.*
import es.edufdezsoy.manga2kindle.data.model.Manga

@Dao
interface MangaDao {
    @Insert
    suspend fun insert(manga: Manga): Long

    @Update
    suspend fun update(manga: Manga)

    @Delete
    suspend fun delete(manga: Manga)

    @Query("DELETE FROM Manga")
    suspend fun deleteAll()

    @Query("SELECT * FROM Manga WHERE mangaId = :id")
    suspend fun get(id: Long): Manga

    @Query("SELECT * FROM Manga WHERE title LIKE :title LIMIT 1")
    suspend fun search(title: String): Manga?
}