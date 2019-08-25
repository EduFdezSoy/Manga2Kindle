package es.edufdezsoy.manga2kindle.ui.observedFolders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.model.Folder
import es.edufdezsoy.manga2kindle.ui.observedFolders.folderForm.FolderFormController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ObservedFoldersController : Controller(), CoroutineScope, ObservedFoldersContract.Controller,
    ObservedFoldersInteractor.Controller {
    //#region vars and vals

    private val interactor = ObservedFoldersInteractor(this)
    private lateinit var view: ObservedFoldersView
    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    //#endregion
    //#region lifecycle methods

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val v = inflater.inflate(R.layout.view_observed_folders, container, false)

        job = Job()
        view = ObservedFoldersView(view = v, controller = this)
        return v
    }

    override fun onDestroyView(view: View) {
        job.cancel()
        super.onDestroyView(view)
    }

    //#endregion
    //#region public methods

    override fun loadFolders() {
        launch {
            interactor.loadMockFolders()
        }
    }

    override fun openFolderDetails(folder: Folder) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun openFolderForm() {
        router.pushController(
            RouterTransaction.with(FolderFormController())
                .pushChangeHandler(overriddenPushHandler)
                .popChangeHandler(overriddenPopHandler)
        )
    }

    override fun deleteFolder(folder: Folder) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setFolders(folders: List<Folder>) {
        view.setFolders(folders)
    }

    //#endregion
}