package es.edufdezsoy.manga2kindle.ui.observedFolders

import es.edufdezsoy.manga2kindle.data.M2kDatabase
import es.edufdezsoy.manga2kindle.data.model.Folder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ObservedFoldersInteractor(val controller: Controller, val database: M2kDatabase) {
    interface Controller {
        fun setFolders(folders: List<Folder>)
        fun loadFolders()
    }

    suspend fun loadFolders() {
        val folders = database.FolderDao().getAll()
        controller.setFolders(folders)
    }

    suspend fun addFolder(folder: Folder) {
        database.FolderDao().insert(folder).also { controller.loadFolders() }
    }

    suspend fun deleteFoldere(folder: Folder) {
        database.FolderDao().delete(folder).also { controller.loadFolders() }
    }
}