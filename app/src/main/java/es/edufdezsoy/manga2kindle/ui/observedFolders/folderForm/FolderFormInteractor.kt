package es.edufdezsoy.manga2kindle.ui.observedFolders.folderForm

import android.content.Context
import es.edufdezsoy.manga2kindle.data.model.Folder
import es.edufdezsoy.manga2kindle.data.repository.FolderRepository

class FolderFormInteractor(val controller: Controller, context: Context) {
    interface Controller {
        fun done()
    }

    private val foldereRepository = FolderRepository.invoke(context)

    suspend fun addFolder(folder: Folder) {
        var i = foldereRepository.getLastIndex()
        if (i == null) i = 1
        folder.id = i + 1
        foldereRepository.insert(folder).also { controller.done() }
    }

    suspend fun updateFolder(folder: Folder) {
        foldereRepository.update(folder).also { controller.done() }
    }

    suspend fun deleteFoldere(folder: Folder) {
        foldereRepository.delete(folder).also { controller.done() }
    }
}