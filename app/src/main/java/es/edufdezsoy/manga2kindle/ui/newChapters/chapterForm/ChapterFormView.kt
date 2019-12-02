package es.edufdezsoy.manga2kindle.ui.newChapters.chapterForm

import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import es.edufdezsoy.manga2kindle.R
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
        view.btnUpload.setOnClickListener {
            disableAllButtons()
            saveData()
            mail = view.tietEmail.text.toString()
            controller.sendChapter(chapter, mail)
        }

        //#endregion
        //#region editActions

        // TODO: if something is edited disable the upload button until changes are saved

        //#endregion

        view.actvAuthor.doOnTextChanged { text, start, count, after ->
            controller.searchAuthors(text.toString())
        }

        // TODO: mangas cant be edited by now
        // this disables the manga text field
        view.tietManga.inputType = InputType.TYPE_NULL
    }

    //#region private functions

    private fun disableAllButtons() {
        onEditDisableButtons()
    }

    private fun onEditDisableButtons() {
        view.btnAddAuthor.isEnabled = false
        view.btnUpload.isEnabled = false
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

    private fun setAuthorTextList(authors: List<Author>): Array<String> {
        val authorsStr = ArrayList<String>()

        authors.forEach {
            authorsStr.add(it.toString())
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
                if (it.toString() == authorStr) {
                    author = it
                    return@setOnItemClickListener
                }
            }
        }
    }

    //#endregion
    //#region override functions

    override fun saveData() {
        mail = view.tietEmail.text.toString()

        try {
            chapter.volume = view.etVolume.text.toString().toInt()
        } catch (e: Exception) {
            chapter.volume = null
        }
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
        view.actvAuthor.setText(author.toString())

        // disable the button
        view.btnAddAuthor.isEnabled = false
        // change the color
        view.btnAddAuthor.background.alpha = 50
        view.btnAddAuthor.setTextColor(ContextCompat.getColor(view.context, R.color.btnDisabled))
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