package es.edufdezsoy.manga2kindle.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import es.edufdezsoy.manga2kindle.data.model.Chapter

@Dao
interface ChapterDao {
    @Query("SELECT * FROM chapter")
    suspend fun getAll(): List<Chapter>

    @Query("SELECT * FROM chapter WHERE id = :chapter_id")
    suspend fun getChapter(chapter_id: Int): Chapter

    // NOTE: booleans in sqlite! 0 = false, 1 = true
    @Query("SELECT * FROM chapter WHERE delivered = 0")
    suspend fun getNoUploadedChapters(): List<Chapter>

    @Query("SELECT * FROM chapter WHERE manga_id = :manga_id AND chapter = :chapter")
    suspend fun search(manga_id: Int, chapter: Float): Chapter?

    @Insert
    suspend fun insert(chapter: Chapter)

    @Update
    suspend fun update(chapter: Chapter)

    @Query("UPDATE chapter SET visible = 0 WHERE id = :chapter_id")
    suspend fun delete(chapter_id: Int)
}