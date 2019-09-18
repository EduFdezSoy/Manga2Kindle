package es.edufdezsoy.manga2kindle.ui.newChapters.chapterForm

import android.view.View
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.data.model.Manga
import kotlinx.android.synthetic.main.view_chapter_form.view.*

class ChapterFormView(val view: View, val controller: ChapterFormContract.Controller) :
    ChapterFormContract.View {
    private lateinit var chapter: Chapter
    private lateinit var manga: Manga


    init {

    }

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

    override fun setMail(mail: String) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}