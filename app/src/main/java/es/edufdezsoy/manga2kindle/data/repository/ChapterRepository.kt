package es.edufdezsoy.manga2kindle.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import es.edufdezsoy.manga2kindle.data.ChapterDatabase
import es.edufdezsoy.manga2kindle.data.dao.ChapterDao
import es.edufdezsoy.manga2kindle.data.model.Chapter

class ChapterRepository(application: Application) {
    private val chapterDao: ChapterDao
    private val allNotes: LiveData<List<Chapter>>

    init {
        val database = ChapterDatabase.getInstance(application.applicationContext)
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

    fun getAllNotes(): LiveData<List<Chapter>> {
        return allNotes
    }
}