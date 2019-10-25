package es.edufdezsoy.manga2kindle.ui.uploadedChapters

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.model.viewObject.UploadedChapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class UploadedChaptersController : Controller(), CoroutineScope,
    UploadedChaptersContract.Controller, UploadedChaptersInteractor.Controller {
    //#region vars and vals

    private lateinit var interactor: UploadedChaptersInteractor
    lateinit var view: UploadedChaptersView
    lateinit var context: Context
    lateinit var handler: Handler
    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    //#endregion
    //#region lifecycle methods

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val v = inflater.inflate(R.layout.view_uploaded_chapters, container, false)

        interactor = UploadedChaptersInteractor(this, v.context)

        job = Job()
        handler = Handler()
        context = v.context
        view = UploadedChaptersView(view = v, controller = this)

        return v
    }

    override fun onDestroyView(view: View) {
        handler.removeCallbacksAndMessages(null)
        job.cancel()
        interactor.close(context)
        super.onDestroyView(view)
    }

    //#endregion
    //#region override methods

    override fun loadChapters() {
        launch {
            interactor.loadChapters()
            interactor.updateStatus(context)
        }
    }

    override fun reloadChapters() {
        launch {
            handler.removeCallbacksAndMessages(null)
            interactor.updateStatus(context)
        }
    }

    override fun openChapterDetails(chapter: UploadedChapter) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideChapter(chapter: UploadedChapter) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setNewChapters(chapters: List<UploadedChapter>) {
        val ar = chapters as ArrayList
        ar.sortWith(compareBy({ it.upload_date }, { it.server_id }))
        ar.reverse()

        launch(Dispatchers.Main) {
            view.setChapters(ar)
        }
    }

    override fun updateList() {
        launch {
            interactor.loadChapters()
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({ loadChapters() }, 10000)
        }
    }
//#endregion
}