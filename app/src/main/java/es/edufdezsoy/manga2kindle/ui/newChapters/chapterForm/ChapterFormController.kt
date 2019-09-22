package es.edufdezsoy.manga2kindle.ui.newChapters.chapterForm

import android.view.LayoutInflater
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
    private lateinit var chapter: Chapter
    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    //#endregion
    //#region Constructors

    constructor() : super()

    constructor(chapter: Chapter) : super() {
        this.chapter = chapter
    }

    //#endregion
    //#region lifecycle methods

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val v = inflater.inflate(R.layout.view_chapter_form, container, false)
        interactor = ChapterFormInteractor(this, M2kDatabase.invoke(v.context))

        job = Job()
        view = ChapterFormView(view = v, controller = this)

        view.setChapter(chapter)
        launch { interactor.getManga(chapter.manga_id) }

        return v
    }

    override fun onDestroyView(view: View) {
        job.cancel()
        super.onDestroyView(view)
    }

    //#endregion
    //#region public methods

    /**
     * Called from the view
     */
    override fun saveData(chapter: Chapter, manga: Manga, mail: String?) {
        launch { interactor.saveChapter(chapter) }
        launch { interactor.saveManga(manga) }
        if (mail != null)
            launch { interactor.saveMail(mail) }
    }

    /**
     * Called from the view
     */
    override fun sendChapter(chapter: Chapter, mail: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
    override fun setManga(manga: Manga) {
        view.setManga(manga)
        launch {
            if (manga.author_id != null)
                interactor.getAuthor(manga.author_id)
        }
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