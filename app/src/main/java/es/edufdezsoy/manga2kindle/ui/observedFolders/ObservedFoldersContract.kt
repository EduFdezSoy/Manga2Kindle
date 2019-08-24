package es.edufdezsoy.manga2kindle.ui.observedFolders

import es.edufdezsoy.manga2kindle.data.model.Folder

interface ObservedFoldersContract {
    interface Controller {
        fun loadFolders()
        fun openFolderDetails(folder: Folder)
        fun deleteFolder(folder: Folder)
    }

    interface View {
        fun setFolders(folders: List<Folder>)
    }
}