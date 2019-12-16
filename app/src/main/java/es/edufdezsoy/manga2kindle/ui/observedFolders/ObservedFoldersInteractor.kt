package es.edufdezsoy.manga2kindle.ui.observedFolders

import android.content.Context
import es.edufdezsoy.manga2kindle.data.model.Folder
import es.edufdezsoy.manga2kindle.data.repository.FolderRepository

class ObservedFoldersInteractor(val controller: Controller, context: Context) {
    interface Controller {
        fun setFolders(folders: List<Folder>)
        fun loadFolders()
    }

    private val foldereRepository = FolderRepository.invoke(context)

    suspend fun loadFolders() {
        val folders = foldereRepository.getAll()
        controller.setFolders(folders)
    }

    suspend fun addFolder(folder: Folder) {
        foldereRepository.insert(folder).also { controller.loadFolders() }
    }

    suspend fun deleteFoldere(folder: Folder) {
        foldereRepository.delete(folder).also { controller.loadFolders() }
    }
}