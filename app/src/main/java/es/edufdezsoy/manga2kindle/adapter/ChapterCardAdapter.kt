package es.edufdezsoy.manga2kindle.adapter

import android.content.Context
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.model.ChapterWithManga
import es.edufdezsoy.manga2kindle.ui.newChapters.NoteViewModel
import kotlinx.android.synthetic.main.view_chapter.view.*

class ChapterCardAdapter(val context: Context, owner: LifecycleOwner) {
    //region vars and vals

    private val dialog = MaterialDialog(context, BottomSheet(LayoutMode.MATCH_PARENT))
    private val view: View
    private lateinit var chapter: ChapterWithManga
    private lateinit var chapterViewModel: NoteViewModel

    //endregion
    //region constructor

    init {
        dialog
            .lifecycleOwner(owner)
            .cornerRadius(16f)
            .customView(R.layout.view_chapter)

        view = dialog.getCustomView()
    }

    //endregion
    //region public functions

    fun setChapter(chapter: ChapterWithManga, chapterViewModel: NoteViewModel) {
        this.chapter = chapter
        this.chapterViewModel = chapterViewModel

        setChapter()
        setListeners()
    }

    fun show() {
        dialog.show()
    }

    //endregion
    //region private functions

    private fun setChapter() {
/*        view.series_textInputLayoutLayout.editText?.setText(chapter.manga_title)
        view.author_textInputLayout.editText?.setText(chapter.author)
        view.title_textInputLayout.editText?.setText(chapter.title)
        view.chapter_textInputLayout.editText?.setText(chapter.chapter.toString())
        view.volume_textInputLayout.editText?.setText(chapter.volume?.toString())*/
    }

    private fun setListeners() {
        view.cancel_button.setOnClickListener {
            saveChapter()
            dialog.cancel()
        }
        view.upload_button.setOnClickListener {
            saveChapter()
            // TODO: upload the chapter or mark as pending to upload
            dialog.cancel()
        }
    }

    private fun saveChapter() {
/*        chapter.manga_title = view.series_textInputLayoutLayout.editText?.text.toString()
        chapter.author = view.author_textInputLayout.editText?.text.toString()
        chapter.title = view.title_textInputLayout.editText?.text.toString()
        chapter.chapter = view.chapter_textInputLayout.editText?.text.toString().toInt()
        chapter.volume = view.volume_textInputLayout.editText?.text.toString().toIntOrNull()*/

        chapterViewModel.update(chapter.chapter)
    }

    private fun resetChapter() {
        setChapter()
    }

    //endregion
}