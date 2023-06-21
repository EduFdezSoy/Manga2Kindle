package es.edufdezsoy.manga2kindle.data.dao

import androidx.room.*
import es.edufdezsoy.manga2kindle.data.model.Manga

@Dao
interface MangaDao {
    @Insert
    fun insert(manga: Manga): Long

    @Update
    fun update(manga: Manga)

    @Delete
    fun delete(manga: Manga)

    @Query("DELETE FROM Manga")
    fun deleteAll()

    @Transaction
    @Query("SELECT * FROM Manga WHERE mangaId = :id")
    fun get(id: Long): Manga

    @Transaction
    @Query("SELECT * FROM Manga WHERE title LIKE :title LIMIT 1")
    fun search(title: String): Manga?
}