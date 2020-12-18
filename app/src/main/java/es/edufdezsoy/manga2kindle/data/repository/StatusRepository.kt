package es.edufdezsoy.manga2kindle.data.repository

import android.app.Application
import es.edufdezsoy.manga2kindle.data.M2KDatabase
import es.edufdezsoy.manga2kindle.data.dao.ChapterDao
import es.edufdezsoy.manga2kindle.data.dao.StatusDao
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.data.model.Status
import kotlinx.coroutines.flow.Flow

class StatusRepository(application: Application) {
    private val statusDao: StatusDao
    private val allStatuses: Flow<List<Status>>

    init {
        val database = M2KDatabase.getInstance(application.applicationContext)
        statusDao = database.statusDao()

        allStatuses = statusDao.getAllStatuses()
    }

    suspend fun insert(status: Status) {
        statusDao.insert(status)
    }

    suspend fun update(status: Status) {
        statusDao.update(status)
    }

    suspend fun delete(status: Status) {
        statusDao.delete(status)
    }

    fun getAllStatuses(): Flow<List<Status>> {
        return allStatuses
    }

    suspend fun getById(id: Int): Status? {
        return statusDao.getById(id)
    }
}