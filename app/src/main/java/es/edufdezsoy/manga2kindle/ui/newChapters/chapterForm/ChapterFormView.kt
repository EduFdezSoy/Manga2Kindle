package es.edufdezsoy.manga2kindle.ui.newChapters.chapterForm

import android.text.InputType
import android.view.View
import es.edufdezsoy.manga2kindle.data.model.Author
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.data.model.Manga
import kotlinx.android.synthetic.main.view_chapter_form.view.*

class ChapterFormView(val view: View, val controller: ChapterFormContract.Controller) :
    ChapterFormContract.View {
    private lateinit var chapter: Chapter
    private lateinit var manga: Manga
    private lateinit var mail: String

    init {
        //#region clickListeners

        view.btnAddAuthor.setOnClickListener { controller.openAuthorForm() }
        view.btnReturn.setOnClickListener { controller.cancelEdit() }
        view.btnUpload.setOnClickListener {
            disableAllButtons()
            mail = view.tietEmail.text.toString()
            controller.sendChapter(chapter, mail)
        }
        view.btnSave.setOnClickListener {
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
        // manga.author_id = // TODO: same here with the author. May add or search it in the database before adding the id

            controller.saveData(chapter, manga, mail)
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

        view.tietAuthor.inputType = InputType.TYPE_NULL
        view.tietAuthor.setText(authorText)
    }

    override fun setMail(mail: String) {
        this.mail = mail
        view.tietEmail.setText(mail)
    }

    //#endregion
}