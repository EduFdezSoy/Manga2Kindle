package es.edufdezsoy.manga2kindle.data.dao

import androidx.room.*
import es.edufdezsoy.manga2kindle.data.model.Chapter
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {
    @Insert
    fun insert(chapter: Chapter)

    @Update
    fun update(chapter: Chapter)

    @Delete
    fun delete(chapter: Chapter)

    @Query("DELETE FROM Chapter")
    fun deleteAllChapters()

    @Query("SELECT * FROM Chapter ORDER BY mangaId DESC, chapter")
    fun getAllChapters(): Flow<List<Chapter>>

    @Query("SELECT * FROM Chapter")
    fun getStaticAllChapters(): List<Chapter>

    @Query("SELECT * FROM Chapter WHERE mangaId = :mangaId AND chapter = :chapterNum")
    fun search(mangaId: Int, chapterNum: Float): Chapter?

    @Query("SELECT * FROM Chapter WHERE rowid = :id")
    fun getById(id: Int): Chapter?

    @Query("SELECT * FROM Chapter WHERE id = :id")
    fun getByRemoteId(id: String): Chapter?
}