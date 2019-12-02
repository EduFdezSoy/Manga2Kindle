package es.edufdezsoy.manga2kindle.ui.observedFolders.folderForm

import android.net.Uri
import android.view.View
import es.edufdezsoy.manga2kindle.data.model.Folder
import kotlinx.android.synthetic.main.view_folder_form.view.*

class FolderFormView(val view: View, val controller: FolderFormContract.Controller) :
    FolderFormContract.View {
    private var folder = Folder(0, "", "")

    init {
        view.btnPath.setOnClickListener { controller.openFolderPicker() }
    }

    override fun setFolder(folder: Folder) {
        this.folder = folder

        view.etName.setText(folder.name)
        view.tvPath.text = Uri.parse(folder.path).path
    }

    override fun setPath(path: String) {
        folder.path = path
        val readablePath = Uri.parse(path).path!!
        view.tvPath.text = readablePath

        if (view.etName.text.isNullOrBlank()) {
            view.etName.setText(readablePath.substring(readablePath.lastIndexOf('/') + 1))
        }
    }

    override fun saveFolder() {
        folder.name = view.etName.text.toString()
        folder.path = folder.path
        controller.saveFolder(folder)
    }

    override fun deleteFolder() {
        controller.deleteFolder(folder)
    }
}
