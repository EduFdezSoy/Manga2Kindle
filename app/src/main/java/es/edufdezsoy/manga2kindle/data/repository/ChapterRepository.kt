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

    fun insert(chapter: Chapter) {
        chapterDao.insert(chapter)
    }

    fun update(chapter: Chapter) {
        chapterDao.update(chapter)
    }

    fun delete(chapter: Chapter) {
        chapterDao.delete(chapter)
    }

    fun deleteAllChapters() {
        chapterDao.deleteAllChapters()
    }

    fun getAllChapters(): Flow<List<Chapter>> {
        return allChapters
    }

    fun getStaticAllChapters(): List<Chapter> {
        return chapterDao.getStaticAllChapters()
    }

    suspend fun search(mangaId: Int, chapterNum: Float): Chapter? {
        return chapterDao.search(mangaId, chapterNum)
    }

    fun getById(chapterId: Int): Chapter? {
        return chapterDao.getById(chapterId)
    }

    fun getByRemoteId(chapterRemoteId: String): Chapter? {
        return chapterDao.getByRemoteId(chapterRemoteId)
    }
}