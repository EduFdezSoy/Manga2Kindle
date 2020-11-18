package es.edufdezsoy.manga2kindle.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.Transaction
import es.edufdezsoy.manga2kindle.data.M2KDatabase
import es.edufdezsoy.manga2kindle.data.dao.ChapterDao
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.data.model.ChapterWithManga

class ChapterRepository(application: Application) {
    private val chapterDao: ChapterDao
     private val allNotes: LiveData<List<ChapterWithManga>>

    init {
        val database = M2KDatabase.getInstance(application.applicationContext)
        chapterDao = database.chapterDao()

         allNotes = chapterDao.getAllNotes()
    }

    suspend fun insert(chapter: Chapter) {
        chapterDao.insert(chapter)
    }

    suspend fun update(chapter: Chapter) {
        chapterDao.update(chapter)
    }

    suspend fun delete(chapter: Chapter) {
        chapterDao.delete(chapter)
    }

    suspend fun deleteAllNotes() {
        chapterDao.deleteAllNotes()
    }

    @Transaction
    fun getAllNotes(): LiveData<List<ChapterWithManga>> {
        return allNotes
    }
}