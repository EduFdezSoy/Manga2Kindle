package es.edufdezsoy.manga2kindle.ui.newChapters.chapterForm

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.M2kDatabase
import es.edufdezsoy.manga2kindle.data.model.Author
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.data.model.Manga
import es.edufdezsoy.manga2kindle.ui.newChapters.chapterForm.authorForm.AuthorFormController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ChapterFormController : Controller, CoroutineScope,
    ChapterFormContract.Controller, ChapterFormInteractor.Controller {
    //#region vals and vars

    private lateinit var interactor: ChapterFormInteractor
    private lateinit var view: ChapterFormView
    private var chapter_id: Int = 0
    private var manga_id: Int = 0
    private var author_id: Int = 0
    private lateinit var chapter: Chapter
    private lateinit var context: Context
    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    //#endregion
    //#region Constructors

    constructor() : super()

    constructor(chapter_id: Int, manga_id: Int, author_id: Int) : super() {
        this.chapter_id = chapter_id
        this.manga_id = manga_id
        this.author_id = author_id
    }

    //#endregion
    //#region lifecycle methods

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val v = inflater.inflate(R.layout.view_chapter_form, container, false)
        interactor = ChapterFormInteractor(this, M2kDatabase.invoke(v.context))
        context = v.context

        job = Job()
        view = ChapterFormView(view = v, controller = this)

        launch {
            interactor.getChapter(chapter_id)
            interactor.getManga(manga_id)

            if (author_id != 0)
                interactor.getAuthor(author_id)
            else
                interactor.getAuthors()

            interactor.getMail(activity!!)
        }

        return v
    }

    override fun onDestroyView(view: View) {
        job.cancel()
        interactor.close(context)
        super.onDestroyView(view)
    }

    //#endregion
    //#region public methods

    /**
     * Called from the activity toolbar
     */
    override fun actionSaveData() {
        view.saveData()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> actionSaveData()
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Called from the view
     */
    override fun saveData(chapter: Chapter, manga: Manga, mail: String?) {
        val done = arrayOf(false, false, false)
        launch {
            interactor.saveChapter(chapter).also {
                done[0] = true
                if (done[0] && done[1] && done[2])
                    done()
            }
        }
        launch {
            interactor.saveManga(manga).also {
                done[1] = true
                if (done[0] && done[1] && done[2])
                    done()
            }
        }
        if (mail != null)
            launch {
                interactor.saveMail(activity!!, mail).also {
                    done[2] = true
                    if (done[0] && done[1] && done[2])
                        done()
                }
            }
        else {
            done[2] = true
            if (done[0] && done[1] && done[2])
                done()
        }
    }

    /**
     * Called from the view
     */
    override fun sendChapter(chapter: Chapter, mail: String) {
        launch {
            interactor.sendChapter(chapter.identifier, mail, context)
        }
    }

    /**
     * Called from the view
     */
    override fun openAuthorForm() {
        router.pushController(
            RouterTransaction.with(AuthorFormController(chapter))
                .pushChangeHandler(overriddenPushHandler)
                .popChangeHandler(overriddenPopHandler)
        )
    }

    /**
     * Called from the view
     */
    override fun cancelEdit() {
        done()
    }

    /**
     * Called from the interactor
     */
    override fun setChapter(chapter: Chapter) {
        this.chapter = chapter
        view.setChapter(chapter)
    }

    /**
     * Called from the interactor
     */
    override fun setManga(manga: Manga) {
        view.setManga(manga)
    }

    /**
     * Called from the interactor
     */
    override fun setAuthor(author: Author) {
        view.setAuthor(author)
    }

    /**
     * Called from the interactor
     */
    override fun setAuthors(authors: List<Author>) {
        view.setAuthors(authors)
    }

    /**
     * Called from the interactor
     */
    override fun setMail(mail: String?) {
        if (mail != null)
            view.setMail(mail)
    }

    /**
     * Called from the interactor
     */
    override fun done() {
        activity!!.onBackPressed()
    }

    //#endregion

}