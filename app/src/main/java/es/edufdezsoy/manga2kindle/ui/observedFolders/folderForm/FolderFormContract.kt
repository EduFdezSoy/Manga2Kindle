package es.edufdezsoy.manga2kindle.ui.observedFolders.folderForm

import es.edufdezsoy.manga2kindle.data.model.Folder

interface FolderFormContract {
    interface Controller {
        fun saveFolder(folder: Folder)
        fun deleteFolder(folder: Folder)
        fun openFolderPicker()
        fun cancelEdit()
    }

    interface View {
        fun setFolder(folder: Folder)
        fun setPath(path: String)
        fun saveFolder()
        fun deleteFolder()
    }
}