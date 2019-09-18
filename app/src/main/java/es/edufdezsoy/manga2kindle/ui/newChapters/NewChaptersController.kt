package es.edufdezsoy.manga2kindle.ui.newChapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.M2kDatabase
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.ui.newChapters.chapterForm.ChapterFormController
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
    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    //#endregion
    //#region lifecycle methods

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val v = inflater.inflate(R.layout.view_new_chapters, container, false)

        interactor = NewChaptersInteractor(this, M2kDatabase.invoke(v.context))

        job = Job()
        view = NewChaptersView(view = v, controller = this)

        return v
    }

    override fun onDestroyView(view: View) {
        job.cancel()
        super.onDestroyView(view)
    }

    //#endregion
    //#region override methods

    override fun loadChapters() {
        launch {
            interactor.loadChapters()
        }
    }

    override fun openChapterDetails(chapter: Chapter) {
        router.pushController(
            RouterTransaction.with(ChapterFormController(chapter))
                .pushChangeHandler(overriddenPushHandler)
                .popChangeHandler(overriddenPopHandler)
        )
    }

    override fun hideChapter(chapter: Chapter) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setNewChapters(chapters: List<Chapter>) {
        (chapters as ArrayList).sortWith(compareBy({ it.manga_id }, { it.chapter }))
        view.setChapters(chapters)
    }

    //#endregion
}