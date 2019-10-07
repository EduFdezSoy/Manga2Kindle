package es.edufdezsoy.manga2kindle.ui.newChapters

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.M2kDatabase
import es.edufdezsoy.manga2kindle.data.model.viewObject.NewChapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class NewChaptersController : Controller(), CoroutineScope, NewChaptersContract.Controller,
    NewChaptersInteractor.Controller {
    //#region vars and vals

    private lateinit var interactor: NewChaptersInteractor
    lateinit var view: NewChaptersView
    lateinit var context: Context
    lateinit var handler: Handler
    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    //#endregion
    //#region lifecycle methods

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val v = inflater.inflate(R.layout.view_new_chapters, container, false)

        interactor = NewChaptersInteractor(this, M2kDatabase.invoke(v.context))

        job = Job()
        handler = Handler()
        context = v.context
        view = NewChaptersView(view = v, controller = this)

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
            interactor.updateChapters(context)
        }
    }

    override fun reloadChapters() {
        launch {
            handler.removeCallbacksAndMessages(null)
            interactor.updateChapters(context)
        }
    }

    override fun openChapterDetails(chapter: NewChapter) {
//        router.pushController(
//            RouterTransaction.with(ChapterFormController(chapter))
//                .pushChangeHandler(overriddenPushHandler)
//                .popChangeHandler(overriddenPopHandler)
//        )
    }

    override fun hideChapter(chapter: NewChapter) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setNewChapters(chapters: List<NewChapter>) {
        (chapters as ArrayList).sortWith(compareBy({ it.manga_id }, { it.chapter }))

        launch(Dispatchers.Main) {
            view.setChapters(chapters)
        }
    }

    override fun updateList() {
        launch {
            interactor.loadChapters()
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({ loadChapters() }, 60000)
        }
    }

    //#endregion
}