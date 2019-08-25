package es.edufdezsoy.manga2kindle.ui.observedFolders.folderForm

import es.edufdezsoy.manga2kindle.data.model.Folder

interface FolderFormContract {
    interface Controller {
        fun addFolder(folder: Folder)
        fun editFolder(folder: Folder)
        fun deleteFolder(folder: Folder)
    }

    interface View {
        fun setFolder(folder: Folder)
    }
}