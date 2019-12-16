package es.edufdezsoy.manga2kindle.ui.hiddenChapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bluelinelabs.conductor.Controller
import com.google.android.material.snackbar.Snackbar
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.model.viewObject.HiddenChapter
import es.edufdezsoy.manga2kindle.ui.newChapters.chapterForm.ChapterFormActivity
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class HiddenChaptersController : Controller(), CoroutineScope, HiddenChaptersContract.Controller,
    HiddenChaptersInteractor.Controller {

    private lateinit var context: Context
    private lateinit var interactor: HiddenChaptersInteractor
    private lateinit var v: View
    private lateinit var view: HiddenChaptersContract.View
    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        v = inflater.inflate(R.layout.view_hidden_chapters, container, false)
        context = v.context
        interactor = HiddenChaptersInteractor(this, v.context)
        job = Job()
        view = HiddenChaptersView(v, this)

        return v
    }

    override fun onDestroyView(view: View) {
        job.cancel()
        super.onDestroyView(view)
    }

    /**
     * Called from the interactor
     */
    override fun setChapters(chapters: List<HiddenChapter>) {
        launch(Dispatchers.Main) {
            view.setChapters(chapters)
        }
    }

    /**
     * Called from the view and interactor
     */
    override fun loadChapters() {
        launch {
            interactor.loadChapters()
        }
    }

    /**
     * Called from the view
     */
    override fun openChapterDetails(chapter: HiddenChapter) {
        if (chapter.server_id == null) {
            val intent = Intent(context, ChapterFormActivity::class.java)

            intent.putExtra(ChapterFormActivity.CHAPTER_KEY, chapter.local_id)
            intent.putExtra(ChapterFormActivity.MANGA_KEY, chapter.manga_id)
            intent.putExtra(ChapterFormActivity.AUTHOR_KEY, chapter.author_id)

            context.startActivity(intent)
        } else {
            // TODO("not implemented, open same as in uploaded chapters")
            Toast.makeText(context, "Not implemented!", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Called from the view
     */
    override fun showChapter(chapter: HiddenChapter) {
        Snackbar.make(v, context.getString(R.string.hidden_chapters_chapter_returned), Snackbar.LENGTH_SHORT)
            .setAction(context.getString(R.string.action_undo)) { hideChapter(chapter) }
            .show()
        GlobalScope.launch(Dispatchers.IO) {
            interactor.showChapter(chapter)
        }
    }

    /**
     * Called from the view
     */
    override fun hideChapter(chapter: HiddenChapter) {
        GlobalScope.launch(Dispatchers.IO) {
            interactor.hideChapter(chapter)
        }
    }
}