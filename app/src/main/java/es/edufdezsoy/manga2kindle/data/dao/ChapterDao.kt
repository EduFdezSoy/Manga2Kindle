package es.edufdezsoy.manga2kindle.data.dao

import androidx.room.*
import es.edufdezsoy.manga2kindle.data.model.Chapter
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {
    @Insert
    suspend fun insert(chapter: Chapter)

    @Update
    suspend fun update(chapter: Chapter)

    @Delete
    suspend fun delete(chapter: Chapter)

    @Query("DELETE FROM Chapter")
    suspend fun deleteAllChapters()

    @Query("SELECT * FROM Chapter ORDER BY mangaId DESC, chapter")
    fun getAllChapters(): Flow<List<Chapter>>

    @Query("SELECT * FROM Chapter")
    suspend fun getStaticAllChapters(): List<Chapter>

    @Query("SELECT * FROM Chapter WHERE mangaId = :mangaId AND chapter = :chapterNum")
    suspend fun search(mangaId: Int, chapterNum: Float): Chapter?

    @Query("SELECT * FROM Chapter WHERE rowid = :id")
    suspend fun getById(id: Int): Chapter?
}