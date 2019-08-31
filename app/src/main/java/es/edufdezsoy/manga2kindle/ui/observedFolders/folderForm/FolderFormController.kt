package es.edufdezsoy.manga2kindle.ui.observedFolders.folderForm

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.M2kDatabase
import es.edufdezsoy.manga2kindle.data.model.Folder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class FolderFormController : Controller, CoroutineScope,
    FolderFormContract.Controller, FolderFormInteractor.Controller {
    //#region vals and vars

    private val READ_REQUEST_CODE = 1
    private lateinit var interactor: FolderFormInteractor
    private lateinit var view: FolderFormView
    private var folder = Folder(0, "", "")
    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    //#endregion
    //#region Constructors

    constructor() : super()

    constructor(folder: Folder) : super() {
        this.folder = folder
    }

    //#endregion
    //#region lifecycle methods

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val v = inflater.inflate(R.layout.view_folder_form, container, false)
        interactor = FolderFormInteractor(this, M2kDatabase.invoke(v.context))

        job = Job()
        view = FolderFormView(view = v, controller = this)

        view.setFolder(folder)

        return v
    }

    override fun onDestroyView(view: View) {
        job.cancel()
        super.onDestroyView(view)
    }

    //#endregion
    //#region public methods

    override fun saveFolder(folder: Folder) {
        launch { interactor.addFolder(folder) }
    }

    override fun cancelEdit() {
        done()
    }

    override fun deleteFolder(folder: Folder) {
        launch { interactor.deleteFoldere(folder) }
    }

    override fun done() {
        activity!!.onBackPressed()
    }

    //#endregion
    //#region File Picker dialog and result

    override fun openFolderPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == READ_REQUEST_CODE && data != null) {
            folder.path = data.data?.toString()!!
            view.setPath(folder.path)
        }
    }

    //#endregion
}