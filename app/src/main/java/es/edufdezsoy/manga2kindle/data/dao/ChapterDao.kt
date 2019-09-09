package es.edufdezsoy.manga2kindle.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import es.edufdezsoy.manga2kindle.data.model.Chapter

@Dao
interface ChapterDao {
    @Query("SELECT * FROM chapter")
    suspend fun getAll(): List<Chapter>

    @Query("SELECT * FROM chapter WHERE id = :chapter_id")
    suspend fun getChapter(chapter_id: Int): Chapter

    @Query("SELECT * FROM chapter WHERE manga_id = :manga_id AND chapter = :chapter")
    suspend fun search(manga_id: Int, chapter: Float)

    @Insert
    suspend fun insert(chapter: Chapter)

    @Query("UPDATE chapter SET visible = 0 WHERE id = :chapter_id")
    suspend fun delete(chapter_id: Int)
}