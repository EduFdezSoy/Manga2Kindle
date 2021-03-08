package es.edufdezsoy.manga2kindle.ui.uploaded

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import es.edufdezsoy.manga2kindle.MainActivity
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.adapter.ChapterBaseAdapter
import es.edufdezsoy.manga2kindle.adapter.ChapterCardAdapter
import es.edufdezsoy.manga2kindle.adapter.UploadedChapterAdapter
import es.edufdezsoy.manga2kindle.data.model.ChapterWithManga
import es.edufdezsoy.manga2kindle.data.model.Status
import es.edufdezsoy.manga2kindle.data.model.UploadChapter
import es.edufdezsoy.manga2kindle.ui.newChapters.ChapterViewModel
import kotlinx.android.synthetic.main.fragment_uploaded_chapters.view.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class UploadedChaptersFragment : Fragment(), ChapterBaseAdapter.OnItemClickListener, ChapterBaseAdapter.OnItemLongClickListener, ChapterCardAdapter.OnUploadItemListener {
    private lateinit var chapterViewModel: ChapterViewModel
    private lateinit var statusViewModel: StatusViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_uploaded_chapters, container, false)

        view.uploaded_chapter_recycler.layoutManager = LinearLayoutManager(context)
        view.uploaded_chapter_recycler.setHasFixedSize(true)

        val adapter = UploadedChapterAdapter()
        view.uploaded_chapter_recycler.adapter = adapter

        chapterViewModel = ViewModelProvider(this).get(ChapterViewModel::class.java)
        statusViewModel = ViewModelProvider(this).get(StatusViewModel::class.java)
        lifecycleScope.launch {
            chapterViewModel.getUploadedChapters().collect {
                adapter.submitList(it)
            }
        }

        adapter.setOnItemClickListener(this)
        adapter.setOnItemLongClickListener(this)

        return view
    }

    override fun onItemClick(chapter: ChapterWithManga) {
        Toast.makeText(context, "click " + chapter.chapter.id, Toast.LENGTH_SHORT).show()
    }

    override fun onItemLongClick(chapter: ChapterWithManga): Boolean {
        Toast.makeText(context, "long click " + chapter.chapter.remoteId, Toast.LENGTH_SHORT).show()
        return true
    }

    override fun onUploadItem(chapter: ChapterWithManga) {
        val uploadChapter = UploadChapter(chapter)
        uploadChapter.email = requireActivity().getSharedPreferences(
            "es.edufdezsoy.manga2kindle_preferences",
            Context.MODE_PRIVATE
        ).getString("kindle_email", "yo@edufdez.es") // TODO: change this
        (activity as MainActivity).uploadChapter(uploadChapter)
    }
}