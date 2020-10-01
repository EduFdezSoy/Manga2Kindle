package es.edufdezsoy.manga2kindle.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import es.edufdezsoy.manga2kindle.data.model.Chapter

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

    @Query("SELECT * FROM Chapter ORDER BY chapter DESC")
    fun getAllNotes(): LiveData<List<Chapter>>
}