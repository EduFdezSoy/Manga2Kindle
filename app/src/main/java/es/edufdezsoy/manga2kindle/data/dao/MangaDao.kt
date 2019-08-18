package es.edufdezsoy.manga2kindle.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import es.edufdezsoy.manga2kindle.data.model.Manga

@Dao
interface MangaDao {
    @Query("SELECT * FROM manga")
    fun getAll(): List<Manga>

    @Query("SELECT * FROM manga WHERE id = :manga_id")
    fun getManga(manga_id: Int): Manga

    // TODO: the params in 'LIKE' maye be in between %, search how to edit the var before performing the query
    @Query("SELECT * FROM manga WHERE title LIKE :search")
    fun search(search: String): List<Manga>

    @Insert
    fun insert(vararg manga: Manga)

    @Delete
    fun delete(manga: Manga)
}