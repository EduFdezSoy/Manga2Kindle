package es.edufdezsoy.manga2kindle.data.repository

import android.app.Application
import es.edufdezsoy.manga2kindle.data.M2KDatabase
import es.edufdezsoy.manga2kindle.data.dao.ChapterDao
import es.edufdezsoy.manga2kindle.data.model.Chapter
import kotlinx.coroutines.flow.Flow

class ChapterRepository(application: Application) {
    private val chapterDao: ChapterDao
    private val allChapters: Flow<List<Chapter>>

    init {
        val database = M2KDatabase.getInstance(application.applicationContext)
        chapterDao = database.chapterDao()

        allChapters = chapterDao.getAllChapters()
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
        chapterDao.deleteAllChapters()
    }

    fun getAllChapters(): Flow<List<Chapter>> {
        return allChapters
    }

    suspend fun getStaticAllChapters(): List<Chapter> {
        return chapterDao.getStaticAllChapters()
    }

    suspend fun search(mangaId: Int, chapterNum: Float): Chapter? {
        return chapterDao.search(mangaId, chapterNum)
    }
}