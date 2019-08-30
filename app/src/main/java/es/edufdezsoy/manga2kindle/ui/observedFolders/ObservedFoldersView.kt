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
        val adapter = FolderAdapter(folders)
        adapter.setOnClickListener(View.OnClickListener { v ->
            controller.openFolderDetails(
                adapter.folders.get(
                    view.rvObservedFolders.getChildAdapterPosition(v)
                )
            )
        })
        view.rvObservedFolders.adapter = adapter
        view.flBackground.visibility = View.GONE
    }
}