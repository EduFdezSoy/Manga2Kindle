package es.edufdezsoy.manga2kindle.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import es.edufdezsoy.manga2kindle.data.model.Chapter

@Dao
interface ChapterDao {
    @Query("SELECT * FROM chapter")
    fun getAll(): List<Chapter>

    @Query("SELECT * FROM chapter WHERE id = :chapter_id")
    fun getChapter(chapter_id: Int): Chapter

    @Insert
    fun insert(chapter: Chapter)

    @Query("UPDATE chapter SET visible = 0 WHERE id = :chapter_id")
    fun delete(chapter_id: Int)
}