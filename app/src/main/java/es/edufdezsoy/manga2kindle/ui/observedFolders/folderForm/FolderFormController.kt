package es.edufdezsoy.manga2kindle.ui.observedFolders.folderForm

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.google.android.material.snackbar.Snackbar
import es.edufdezsoy.manga2kindle.R
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
    private lateinit var context: Context
    private var folder = Folder(0, "", "")
    private var folder_id = 0
    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    //#endregion
    //#region Constructors

    constructor() : super()

    constructor(folder_id: Int) : super() {
        this.folder_id = folder_id
    }

    constructor(folder: Folder) : super() {
        this.folder = folder
    }

    //#endregion
    //#region lifecycle methods

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val v = inflater.inflate(R.layout.view_folder_form, container, false)
        interactor = FolderFormInteractor(this, v.context)

        context = v.context
        job = Job()
        view = FolderFormView(view = v, controller = this)

        if (folder_id != 0)
            launch(Dispatchers.IO) {
                interactor.getFolder(folder_id)
            }

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
        if (folder.path.isBlank())
            Snackbar.make(getView()!!, context.getString(R.string.folder_form_empty_path_error), Snackbar.LENGTH_LONG).show()
        else
            if (folder.id == 0)
                launch { interactor.addFolder(folder) }
            else
                launch { interactor.updateFolder(folder) }
    }

    override fun cancelEdit() {
        done()
    }

    override fun deleteFolder(folder: Folder) {
        Snackbar.make(getView()!!, context.getString(R.string.folder_form_folder_deleted, folder.name), Snackbar.LENGTH_LONG).show()
        launch { interactor.deleteFoldere(folder) }
    }

    override fun setFolder(folder: Folder) {
        this.folder = folder
        view.setFolder(folder)
    }

    override fun done() {
        activity!!.onBackPressed()
    }

    /**
     * toolbar buttons
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> view.saveFolder()
            R.id.action_delete -> view.deleteFolder()
        }

        return super.onOptionsItemSelected(item)
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

            // persistable and prefix (to read contents)
            activity!!.grantUriPermission(
                activity!!.packageName,
                data.data,
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            )
            activity!!.grantUriPermission(
                activity!!.packageName,
                data.data,
                Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
            )

            // read permissions
            activity!!.grantUriPermission(
                activity!!.packageName,
                data.data,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            activity!!.contentResolver.takePersistableUriPermission(
                data.data!!,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            // write permissions
            activity!!.grantUriPermission(
                activity!!.packageName,
                data.data,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            activity!!.contentResolver.takePersistableUriPermission(
                data.data!!,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        }
    }

    //#endregion
}