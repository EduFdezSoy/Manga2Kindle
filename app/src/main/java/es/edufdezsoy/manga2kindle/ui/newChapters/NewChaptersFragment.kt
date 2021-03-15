package es.edufdezsoy.manga2kindle.ui.newChapters

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
import es.edufdezsoy.manga2kindle.adapter.ChapterAdapter
import es.edufdezsoy.manga2kindle.adapter.ChapterBaseAdapter
import es.edufdezsoy.manga2kindle.adapter.ChapterCardAdapter
import es.edufdezsoy.manga2kindle.data.model.ChapterWithManga
import es.edufdezsoy.manga2kindle.data.model.UploadChapter
import es.edufdezsoy.manga2kindle.data.repository.SharedPreferencesHandler
import kotlinx.android.synthetic.main.fragment_new_chapters.view.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NewChaptersFragment : Fragment(), ChapterBaseAdapter.OnItemClickListener,
    ChapterBaseAdapter.OnItemLongClickListener, ChapterCardAdapter.OnUploadItemListener {
    private lateinit var chapterViewModel: ChapterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_chapters, container, false)

        view.new_chapter_recycler.layoutManager = LinearLayoutManager(context)
        view.new_chapter_recycler.setHasFixedSize(true)

        val adapter = ChapterAdapter()
        view.new_chapter_recycler.adapter = adapter

        chapterViewModel = ViewModelProvider(this).get(ChapterViewModel::class.java)
        lifecycleScope.launch {
            chapterViewModel.getNotUploadedChapters().collect {
                adapter.submitList(it)
            }
        }

        adapter.setOnItemClickListener(this)
        adapter.setOnItemLongClickListener(this)

        return view
    }

    override fun onItemClick(chapter: ChapterWithManga) {
        val chapterCard = ChapterCardAdapter(requireContext(), viewLifecycleOwner)
        chapterCard.setOnUploadItemListener(this)
        chapterCard.setChapter(chapter, chapterViewModel)
        chapterCard.show()
    }

    override fun onItemLongClick(chapter: ChapterWithManga): Boolean {
        // Toast.makeText(context, "long click " + note.priority, Toast.LENGTH_SHORT).show()
        return true
    }

    override fun onUploadItem(chapter: ChapterWithManga) {
        val uploadChapter = UploadChapter(chapter)
        uploadChapter.email = SharedPreferencesHandler(requireContext()).kindleEmail
        (activity as MainActivity).uploadChapter(uploadChapter)
    }
}