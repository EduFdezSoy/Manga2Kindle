package es.edufdezsoy.manga2kindle.ui.newChapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.adapter.ChapterAdapter
import es.edufdezsoy.manga2kindle.adapter.ChapterCardAdapter
import es.edufdezsoy.manga2kindle.data.model.ChapterWithManga
import kotlinx.android.synthetic.main.fragment_new_chapters.view.*
import kotlinx.coroutines.launch

class NewChaptersFragment : Fragment(), ChapterAdapter.OnItemClickListener,
    ChapterAdapter.OnItemLongClickListener {
    private lateinit var chapterViewModel: ChapterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_chapters, container, false)

        view.noteList_recycler.layoutManager = LinearLayoutManager(context)
        view.noteList_recycler.setHasFixedSize(true)

        val adapter = ChapterAdapter()
        view.noteList_recycler.adapter = adapter

        chapterViewModel = ViewModelProvider(this).get(ChapterViewModel::class.java)
        lifecycleScope.launch {
            chapterViewModel.getAllNotes().observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }
        }

        adapter.setOnItemClickListener(this)
        adapter.setOnItemLongClickListener(this)

        return view
    }

    override fun onItemClick(chapter: ChapterWithManga) {
        val chapterCard = ChapterCardAdapter(requireContext(), viewLifecycleOwner)
        chapterCard.setChapter(chapter, chapterViewModel)
        chapterCard.show()
    }

    override fun onItemLongClick(chapter: ChapterWithManga) {
        // Toast.makeText(context, "long click " + note.priority, Toast.LENGTH_SHORT).show()
    }
}