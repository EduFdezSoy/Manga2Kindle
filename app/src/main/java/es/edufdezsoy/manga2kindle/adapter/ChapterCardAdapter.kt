package es.edufdezsoy.manga2kindle.adapter

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import es.edufdezsoy.manga2kindle.data.model.Author
import es.edufdezsoy.manga2kindle.data.model.ChapterWithManga
import es.edufdezsoy.manga2kindle.databinding.ViewChapterBinding
import es.edufdezsoy.manga2kindle.ui.newChapters.ChapterViewModel

class ChapterCardAdapter(val context: Context, owner: LifecycleOwner) {
    //region vars and vals

    private val dialog = MaterialDialog(context, BottomSheet(LayoutMode.MATCH_PARENT))
    private val binding: ViewChapterBinding
    private lateinit var chapter: ChapterWithManga
    private lateinit var chapterViewModel: ChapterViewModel
    private var uploadItemListener: OnUploadItemListener? = null

    interface OnUploadItemListener {
        fun onUploadItem(chapter: ChapterWithManga)
    }

    //endregion
    //region constructor

    init {
        dialog
            .lifecycleOwner(owner)
            .cornerRadius(16f)

        binding = ViewChapterBinding.inflate(dialog.layoutInflater)

        dialog.customView(view = binding.root)

    }

    //endregion
    //region public functions

    fun setOnUploadItemListener(listener: OnUploadItemListener) {
        uploadItemListener = listener
    }

    fun setChapter(chapter: ChapterWithManga, chapterViewModel: ChapterViewModel) {
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
        binding.seriesTextInputLayoutLayout.editText?.setText(chapter.manga.manga.title)
        binding.authorTextInputLayout.editText?.setText(chapter.manga.authorsToString())
        binding.titleTextInputLayout.editText?.setText(chapter.chapter.title)
        binding.chapterTextInputLayout.editText?.setText(chapter.chapter.chapterToString())
        binding.volumeTextInputLayout.editText?.setText(chapter.chapter.volume?.toString())
    }

    private fun setListeners() {
        binding.cancelButton.setOnClickListener {
            saveChapter()
            dialog.cancel()
        }
        binding.uploadButton.setOnClickListener {
            saveChapter()
            uploadItemListener?.onUploadItem(chapter)
            dialog.cancel()
        }
    }

    private fun saveChapter() {
        chapter.manga.manga.title = binding.seriesTextInputLayoutLayout.editText?.text.toString()
        chapter.chapter.title = binding.titleTextInputLayout.editText?.text.toString()
        chapter.chapter.chapter = binding.chapterTextInputLayout.editText?.text.toString().toFloat()
        chapter.chapter.volume =
            binding.volumeTextInputLayout.editText?.text.toString().toIntOrNull()

        val authorsStr = binding.authorTextInputLayout.editText?.text.toString().split(",")
        val authors = ArrayList<Author>()
        authorsStr.forEach {
            authors.add(Author(it.trim()))
        }
        chapter.manga.authors = authors

        // TODO: add manga and author view models and save all
        chapterViewModel.update(chapter.chapter)
    }

    private fun resetChapter() {
        setChapter()
    }

    //endregion
}