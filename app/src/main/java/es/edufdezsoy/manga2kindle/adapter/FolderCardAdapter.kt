package es.edufdezsoy.manga2kindle.adapter

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.model.Folder
import es.edufdezsoy.manga2kindle.ui.watchedFolders.FolderViewModel
import kotlinx.android.synthetic.main.view_folder.view.*

class FolderCardAdapter(val context: Context, owner: LifecycleOwner) {
    //region vars and vals

    private val dialog = MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT))
    private val view: View
    private lateinit var folder: Folder
    private lateinit var folderViewModel: FolderViewModel

    //endregion
    //region constructor

    init {
        dialog
            .lifecycleOwner(owner)
            .cornerRadius(16f)
            .customView(R.layout.view_folder)

        view = dialog.getCustomView()
    }

    //endregion
    //region public functions

    fun setFolder(folder: Folder, folderViewModel: FolderViewModel) {
        this.folder = folder
        this.folderViewModel = folderViewModel

        setFolder()
        setListeners()
    }

    fun show() {
        dialog.show()
    }

    //endregion
    //region private functions

    private fun setFolder() {
        Log.d("a", folder.toString())

        view.title_textInputLayout.editText?.setText(folder.name)
        view.path_textInputLayout.editText?.setText(folder.path)
        view.active_switch.isChecked = folder.active
    }

    private fun setListeners() {
        view.delete_button.setOnClickListener {
            folderViewModel.delete(folder)
            dialog.cancel()
        }
        view.upload_button.setOnClickListener {
            saveFolder()
            dialog.cancel()
        }
    }

    private fun saveFolder() {
        folder.name = view.title_textInputLayout.editText?.text.toString()
        folder.active = view.active_switch.isChecked

        folderViewModel.update(folder)
    }

    //endregion
}