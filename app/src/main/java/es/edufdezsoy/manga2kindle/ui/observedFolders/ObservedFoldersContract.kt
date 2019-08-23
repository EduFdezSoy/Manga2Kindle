package es.edufdezsoy.manga2kindle.ui.observedFolders

interface ObservedFoldersContract {
    interface Controller {
        fun loadFolders()
        fun openFolderDetails()
        fun deleteFolder()
    }

    // TODO: there is no Folder object !!
    interface View {
        fun setFolders()
    }
}