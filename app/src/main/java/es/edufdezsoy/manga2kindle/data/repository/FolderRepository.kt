package es.edufdezsoy.manga2kindle.data.repository

import android.content.Context
import es.edufdezsoy.manga2kindle.M2kApplication
import es.edufdezsoy.manga2kindle.data.M2kDatabase
import es.edufdezsoy.manga2kindle.data.model.Folder

class FolderRepository {
    private val TAG = M2kApplication.TAG + "_FolderRepo"
    private val folderList = ArrayList<Folder>()
    private val database: M2kDatabase

    //#region object init

    companion object {
        @Volatile
        private var instance: FolderRepository? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: FolderRepository(context).also { instance = it }
        }
    }

    private constructor(context: Context) {
        this.database = M2kDatabase.invoke(context)
    }

    //#endregion
    //#region public methods

    suspend fun get(id: Int): Folder {
        return database.FolderDao().getFolderById(id)
    }

    suspend fun getAll(): ArrayList<Folder> {
        if (folderList.isEmpty())
            folderList.addAll(database.FolderDao().getAll())

        return folderList
    }

    suspend fun getLastIndex(): Int? {
        if (folderList.isNotEmpty()) {
            folderList.sortBy { it.id }
            return folderList[folderList.size - 1].id
        } else {
            val lastId = database.FolderDao().getLastIndex()
            return lastId
        }
    }

    suspend fun insert(folder: Folder) {
        folderList.add(folder)
        database.FolderDao().insert(folder)
    }

    suspend fun update(folder: Folder) {
        folderList.forEach {
            if (it.id == folder.id)
                folderList.remove(folder)
        }
        folderList.add(folder)
        database.FolderDao().update(folder)
    }

    suspend fun delete(folder: Folder) {
        database.FolderDao().delete(folder)
        folderList.clear()
        folderList.addAll(database.FolderDao().getAll())
    }

    //#endregion
}