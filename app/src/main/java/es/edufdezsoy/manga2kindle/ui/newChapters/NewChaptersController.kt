package es.edufdezsoy.manga2kindle.ui.newChapters

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.google.android.material.snackbar.Snackbar
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.model.viewObject.NewChapter
import es.edufdezsoy.manga2kindle.ui.base.BaseActivity
import es.edufdezsoy.manga2kindle.ui.newChapters.chapterForm.ChapterFormActivity
import kotlinx.coroutines.*
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

        interactor = NewChaptersInteractor(this, v.context)

        job = Job()
        handler = Handler()
        context = v.context
        view = NewChaptersView(view = v, controller = this)
        (activity as BaseActivity).scanMangas()
        (activity as BaseActivity).uploadChapters()

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
            (activity as BaseActivity).scanMangas()
        }
    }

    override fun openChapterDetails(chapter: NewChapter) {
        val intent = Intent(context, ChapterFormActivity::class.java)

        intent.putExtra(ChapterFormActivity.CHAPTER_KEY, chapter.local_id)
        intent.putExtra(ChapterFormActivity.MANGA_KEY, chapter.manga_local_id)
        intent.putExtra(ChapterFormActivity.AUTHOR_KEY, chapter.author_id)

        context.startActivity(intent)
    }

    override fun hideChapter(chapter: NewChapter) {
        launch {
            interactor.hideChapter(chapter)
        }

        (activity as BaseActivity).showSnackbar(
            context.getString(R.string.new_chapters_chapter_hidden),
            Snackbar.LENGTH_SHORT,
            context.getString(R.string.action_undo),
            View.OnClickListener {
                GlobalScope.launch(Dispatchers.IO) {
                    interactor.showChapter(chapter)
                }
            }
        )
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
            handler.postDelayed({ loadChapters() }, 5 * 60 * 1000)
        }
    }

    //#endregion
}