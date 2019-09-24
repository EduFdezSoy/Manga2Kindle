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
        view.btnAddAuthor.setOnClickListener { controller.openAuthorForm() }
        view.btnReturn.setOnClickListener { controller.cancelEdit() }
        view.btnUpload.setOnClickListener {
            mail = view.tietEmail.text.toString()
            controller.sendChapter(chapter, mail)
        }
        view.btnSave.setOnClickListener {
            mail = view.tietEmail.text.toString()
            controller.saveData(chapter, manga, mail)
        }
    }

    // TODO: if something is edited disable the upload button until changes are saved

    override fun setChapter(chapter: Chapter) {
        this.chapter = chapter

        if (chapter.volume != null)
            view.etVolume.setText(chapter.volume.toString())

        view.etChapter.setText(chapter.chapter.toString())
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
}