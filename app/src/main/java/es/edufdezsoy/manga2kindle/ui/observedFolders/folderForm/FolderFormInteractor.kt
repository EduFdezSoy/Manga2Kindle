package es.edufdezsoy.manga2kindle.ui.observedFolders.folderForm

import es.edufdezsoy.manga2kindle.data.M2kDatabase
import es.edufdezsoy.manga2kindle.data.model.Folder

class FolderFormInteractor(val controller: FolderFormController, val database: M2kDatabase) {
    suspend fun addFolder(folder: Folder) {
        var i = database.FolderDao().getLastIndex()
        if (i == null) i = 1
        folder.id = i + 1
        database.FolderDao().insert(folder)
    }

    suspend fun deleteFoldere(folder: Folder) {
        database.FolderDao().delete(folder)
    }
}