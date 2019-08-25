package es.edufdezsoy.manga2kindle.ui.observedFolders.folderForm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.model.Folder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class FolderFormController : Controller(), CoroutineScope, FolderFormContract.Controller {
    private lateinit var view: FolderFormView
    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val v = inflater.inflate(R.layout.view_folder_form, container, false)

        job = Job()
        view = FolderFormView(view = v, controller = this)
        return v
    }

    override fun onDestroyView(view: View) {
        job.cancel()
        super.onDestroyView(view)
    }

    override fun addFolder(folder: Folder) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun editFolder(folder: Folder) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteFolder(folder: Folder) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}