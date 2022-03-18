package es.edufdezsoy.manga2kindle.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import es.edufdezsoy.manga2kindle.data.M2KDatabase
import es.edufdezsoy.manga2kindle.data.dao.FolderDao
import es.edufdezsoy.manga2kindle.data.model.Folder

class FolderRepository(application: Application) {
    private val folderDao: FolderDao
    private val allFolders: LiveData<List<Folder>>

    init {
        val database = M2KDatabase.getInstance(application.applicationContext)
        folderDao = database.folderDao()

        allFolders = folderDao.getAllFolders()
    }

    suspend fun insert(folder: Folder) {
        folderDao.insert(folder)
    }

    suspend fun update(folder: Folder) {
        folderDao.update(folder)
    }

    suspend fun delete(folder: Folder) {
        folderDao.delete(folder)
    }

    fun getAllFolders(): LiveData<List<Folder>> {
        return allFolders
    }

    suspend fun getStaticFolderList(): List<Folder> {
        return folderDao.getStaticFolderList()
    }

    suspend fun getStaticActiveFolders(): List<Folder> {
        return folderDao.getStaticActiveFolders()
    }
}