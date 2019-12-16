package es.edufdezsoy.manga2kindle.ui.observedFolders

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import com.google.android.material.snackbar.Snackbar
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.model.Folder
import es.edufdezsoy.manga2kindle.ui.base.BaseActivity
import es.edufdezsoy.manga2kindle.ui.observedFolders.folderForm.FolderFormActivity
import es.edufdezsoy.manga2kindle.ui.observedFolders.folderForm.FolderFormController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ObservedFoldersController : Controller(), CoroutineScope, ObservedFoldersContract.Controller,
    ObservedFoldersInteractor.Controller {
    //#region vars and vals

    private lateinit var interactor: ObservedFoldersInteractor
    private lateinit var view: ObservedFoldersView
    private lateinit var context: Context
    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    //#endregion
    //#region lifecycle methods

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val v = inflater.inflate(R.layout.view_observed_folders, container, false)

        interactor = ObservedFoldersInteractor(this, v.context)

        context = v.context
        job = Job()
        view = ObservedFoldersView(view = v, controller = this)

        return v
    }

    override fun onActivityResumed(activity: Activity) {
        super.onActivityResumed(activity)
        loadFolders()
    }

    override fun onDestroyView(view: View) {
        job.cancel()
        super.onDestroyView(view)
    }

    //#endregion
    //#region public methods

    override fun loadFolders() {
        launch {
            interactor.loadFolders()
        }
    }

    override fun openFolderDetails(folder: Folder) {
        val intent = Intent(context, FolderFormActivity::class.java)
        intent.putExtra(FolderFormActivity.FOLDER_KEY, folder.id)
        startActivity(intent)
    }

    override fun openFolderForm() {
        val intent = Intent(context, FolderFormActivity::class.java)
        startActivity(intent)
    }

    override fun deleteFolder(folder: Folder) {
        launch { interactor.deleteFoldere(folder) }
        (activity as BaseActivity).showSnackbar(context.getString(R.string.folder_form_folder_deleted, folder.name),
            Snackbar.LENGTH_LONG,
            "Undo",
            View.OnClickListener { launch { interactor.addFolder(folder) } })
    }

    override fun setFolders(folders: List<Folder>) {
        view.setFolders(folders)
    }

    //#endregion
}