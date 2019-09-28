package es.edufdezsoy.manga2kindle.ui.newChapters.chapterForm

import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import es.edufdezsoy.manga2kindle.data.model.Author
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.data.model.Manga
import kotlinx.android.synthetic.main.view_chapter_form.view.*

class ChapterFormView(val view: View, val controller: ChapterFormContract.Controller) :
    ChapterFormContract.View {
    private lateinit var chapter: Chapter
    private lateinit var manga: Manga
    private lateinit var mail: String
    private lateinit var author: Author
    private lateinit var authors: List<Author>
    private var authorArray = arrayOf("")

    init {
        //#region clickListeners

        view.btnAddAuthor.setOnClickListener { controller.openAuthorForm() }
        view.btnReturn.setOnClickListener { controller.cancelEdit() }
        view.btnUpload.setOnClickListener {
            disableAllButtons()
            saveData()
            mail = view.tietEmail.text.toString()
            controller.sendChapter(chapter, mail)
        }
        view.btnSave.setOnClickListener {
            disableAllButtons()
            saveData()
            onSaveEnableButtons()
        }

        //#endregion
        //#region editActions

        // TODO: if something is edited disable the upload button until changes are saved

        //#endregion
    }

    //#region private functions

    private fun saveData() {
        mail = view.tietEmail.text.toString()

        chapter.volume = view.etVolume.text.toString().toInt()
        // chapter.chapter = view.etChapter.text.toString().toFloat() // chapter can not be reassigned, we expect the chapter to be correct
        chapter.title = view.tietTitle.text.toString()

        // TODO: manga title cant actually be edited because to the server it will be a new manga
        // manga.title = view.tietManga.text.toString()
        if (manga.author_id == null) {
            val newManga = Manga(manga.id, manga.title, author.id)
            newManga.synchronized = manga.synchronized
            newManga.identifier = manga.identifier

            controller.saveData(chapter, newManga, mail)
        } else {
            controller.saveData(chapter, manga, mail)
        }
    }

    private fun disableAllButtons() {
        onEditDisableButtons()
        view.btnSave.isEnabled = false
    }

    private fun onEditDisableButtons() {
        view.btnAddAuthor.isEnabled = false
        view.btnReturn.isEnabled = false
        view.btnUpload.isEnabled = false
    }

    private fun onSaveEnableButtons() {
        view.btnAddAuthor.isEnabled = true
        view.btnReturn.isEnabled = true
        view.btnUpload.isEnabled = true
    }

    private fun trimTrailingZero(value: String?): String? {
        return if (!value.isNullOrEmpty()) {
            if (value.indexOf(".") < 0) {
                value

            } else {
                value.replace("0*$".toRegex(), "").replace("\\.$".toRegex(), "")
            }

        } else {
            value
        }
    }

    private fun formatAuthor(author: Author): String {
        var authorText = ""
        if (!author.surname.isNullOrEmpty() || !author.name.isNullOrEmpty()) {
            if (!author.surname.isNullOrEmpty())
                authorText += author.surname + " "
            if (!author.name.isNullOrEmpty())
                authorText += author.name + " "
            if (!author.nickname.isNullOrEmpty())
                authorText += "(AKA " + author.nickname + ")"
        } else {
            if (!author.nickname.isNullOrEmpty())
                authorText += author.nickname
        }

        return authorText
    }

    private fun setAuthorTextList(authors: List<Author>): Array<String> {
        val authorsStr = ArrayList<String>()

        authors.forEach {
            authorsStr.add(formatAuthor(it))
        }

        return authorsStr.toTypedArray()
    }

    private fun notifyAuthorAdapter() {
        val adapter = ArrayAdapter<String>(
            view.context,
            android.R.layout.simple_dropdown_item_1line,
            authorArray
        )
        view.actvAuthor.setAdapter(adapter)
        view.actvAuthor.threshold = 1
        view.actvAuthor.setOnItemClickListener { adapterView, view, i, l ->
            val authorStr = adapterView.getItemAtPosition(i)

            authors.forEach {
                if (formatAuthor(it) == authorStr) {
                    author = it
                    return@setOnItemClickListener
                }
            }
        }
    }

    //#endregion
    //#region override functions

    override fun setChapter(chapter: Chapter) {
        this.chapter = chapter

        if (chapter.volume != null)
            view.etVolume.setText(chapter.volume.toString())

        view.etChapter.setText(trimTrailingZero(chapter.chapter.toString()))
        view.tietTitle.setText(chapter.title)
    }

    override fun setManga(manga: Manga) {
        this.manga = manga

        view.tietManga.setText(manga.title)
    }

    override fun setAuthor(author: Author) {
        view.actvAuthor.inputType = InputType.TYPE_NULL
        view.actvAuthor.setText(formatAuthor(author))
    }

    override fun setAuthors(authors: List<Author>) {
        this.authors = authors
        authorArray = setAuthorTextList(authors)
        notifyAuthorAdapter()
    }

    override fun setMail(mail: String) {
        this.mail = mail
        view.tietEmail.setText(mail)
    }

    //#endregion
}