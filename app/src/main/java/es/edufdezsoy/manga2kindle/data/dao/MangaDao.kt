package es.edufdezsoy.manga2kindle.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import es.edufdezsoy.manga2kindle.data.model.Manga

@Dao
interface MangaDao {
    @Query("SELECT * FROM manga")
    suspend fun getAll(): List<Manga>

    @Query("SELECT * FROM manga WHERE id = :manga_id")
    suspend fun getManga(manga_id: Int): Manga

    @Query("SELECT * FROM manga WHERE identifier = :manga_id")
    suspend fun getMangaById(manga_id: Int): Manga

    @Query("SELECT * FROM manga ORDER BY id DESC LIMIT 1")
    suspend fun getLastManga(): Manga?

    // TODO: the params in 'LIKE' maye be in between %, search how to edit the var before performing the query
    @Query("SELECT * FROM manga WHERE title LIKE :search")
    suspend fun search(search: String): List<Manga>

    @Insert
    suspend fun insert(vararg manga: Manga)

    @Delete
    suspend fun delete(manga: Manga)
}