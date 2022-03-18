package es.edufdezsoy.manga2kindle.adapter

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import es.edufdezsoy.manga2kindle.data.model.Folder
import es.edufdezsoy.manga2kindle.databinding.ViewFolderBinding
import es.edufdezsoy.manga2kindle.ui.watchedFolders.FolderViewModel

class FolderCardAdapter(val context: Context, owner: LifecycleOwner) {
    //region vars and vals

    private val dialog = MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT))
    private val binding: ViewFolderBinding
    private lateinit var folder: Folder
    private lateinit var folderViewModel: FolderViewModel

    //endregion
    //region constructor

    init {
        dialog
            .lifecycleOwner(owner)
            .cornerRadius(16f)

        binding = ViewFolderBinding.inflate(dialog.layoutInflater)

        dialog.customView(view = binding.root)
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
        binding.titleTextInputLayout.editText?.setText(folder.name)
        binding.pathTextInputLayout.editText?.setText(folder.path)
        binding.activeSwitch.isChecked = folder.active
    }

    private fun setListeners() {
        binding.deleteButton.setOnClickListener {
            folderViewModel.delete(folder)
            dialog.cancel()
        }
        binding.uploadButton.setOnClickListener {
            saveFolder()
            dialog.cancel()
        }
    }

    private fun saveFolder() {
        folder.name = binding.titleTextInputLayout.editText?.text.toString()
        folder.active = binding.activeSwitch.isChecked

        folderViewModel.update(folder)
    }

    //endregion
}