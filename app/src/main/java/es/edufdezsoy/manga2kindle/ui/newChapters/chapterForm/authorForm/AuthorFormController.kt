package es.edufdezsoy.manga2kindle.ui.newChapters.chapterForm.authorForm

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.model.Author
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.data.model.Manga
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class AuthorFormController : Controller, CoroutineScope, AuthorFormContract.Controller,
    AuthorFormInteractor.Controller {
    //#region vals and vars
    private lateinit var interactor: AuthorFormInteractor
    private lateinit var view: AuthorFormContract.View

    private lateinit var chapter: Chapter
    private var chapter_id = 0
    private var called = ""

    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    //#endregion
    //#region Constructors

    constructor() : super()

    constructor(chapter: Chapter) : super() {
        this.chapter = chapter
    }

    constructor(chapter_id: Int) : super() {
        this.chapter_id = chapter_id
    }

    //#endregion
    //#region lifecycle methods

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val v = inflater.inflate(R.layout.view_author_form, container, false)
        interactor = AuthorFormInteractor(this, v.context)

        job = Job()
        view = AuthorFormView(view = v, controller = this)


        if (::chapter.isInitialized) {
            setChapter(chapter)
        } else if (chapter_id != 0) {
            launch {
                interactor.getChapter(chapter_id)
            }
        }

        return v
    }

    override fun onDestroyView(view: View) {
        job.cancel()
        super.onDestroyView(view)
    }

    //#endregion
    //#region public methods

    /**
     * Called from the activity
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> view.saveAuthor()
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Called from the view
     */
    override fun findNames(str: String) {
        called = "names"

        launch {
            interactor.getAuthors(str)
        }
    }

    /**
     * Called from the view
     */
    override fun findSurnames(str: String) {
        called = "surnames"

        launch {
            interactor.getAuthors(str)
        }
    }

    /**
     * Called from the view
     */
    override fun saveAuthor(name: String, surname: String, nickname: String) {
        if (name.isNotBlank() || surname.isNotBlank() || nickname.isNotBlank())
            launch {
                interactor.saveAuthor(name, surname, nickname)
            }
        else
            done()
    }

    /**
     * Called from the view
     */
    override fun cancelEdit() {
        onBackPressed()
    }

    /**
     * Called from the interactor
     */
    override fun setAuthorList(authors: List<Author>) {
        if (called == "names") {
            val names = ArrayList<String>()

            authors.forEach {
                if (it.name != null)
                    names.add(it.name)
            }

            view.setNameList(names)

        } else if (called == "surnames") {
            val surnames = ArrayList<String>()

            authors.forEach {
                if (it.surname != null)
                    surnames.add(it.surname)
            }

            view.setSurnameList(surnames)
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
    override fun setManga(manga: Manga) {
        launch {
            if (manga.author_id != null)
                interactor.getAuthor(manga.author_id)
        }
    }

    override fun setChapter(chapter: Chapter) {
        launch {
            interactor.getManga(chapter.manga_id)
        }

        this.chapter = chapter
    }


    /**
     * Called from the interactor
     */
    override fun done() {
        onBackPressed()
    }

    //#endregion
    //#region private methods

    private fun onBackPressed() {
        activity!!.onBackPressed()
    }

    //#endregion
}