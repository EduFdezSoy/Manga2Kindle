package es.edufdezsoy.manga2kindle.ui.observedFolders

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import es.edufdezsoy.manga2kindle.data.model.Folder
import es.edufdezsoy.manga2kindle.ui.adapter.FolderAdapter
import kotlinx.android.synthetic.main.view_observed_folders.view.*

class ObservedFoldersView(val view: View, val controller: ObservedFoldersController) :
    ObservedFoldersContract.View {
    init {
        view.rvObservedFolders.layoutManager = LinearLayoutManager(controller.activity)
        view.tvViewTitle.text = "cargando lista..."
        view.fabAddFolder.setOnClickListener { controller.openFolderForm() }

        controller.loadFolders()
    }

    override fun setFolders(folders: List<Folder>) {
        view.rvObservedFolders.adapter = FolderAdapter(folders)
        view.flBackground.visibility = View.GONE
    }
}