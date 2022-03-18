package es.edufdezsoy.manga2kindle.ui.newChapters

import android.os.Bundle
import android.view.*
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
import es.edufdezsoy.manga2kindle.databinding.FragmentNewChaptersBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class NewChaptersFragment : Fragment(), ChapterBaseAdapter.OnItemClickListener,
    ChapterBaseAdapter.OnItemLongClickListener, ChapterCardAdapter.OnUploadItemListener {
    private lateinit var chapterViewModel: ChapterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewChaptersBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)

        binding.newChapterRecycler.layoutManager = LinearLayoutManager(context)
        binding.newChapterRecycler.setHasFixedSize(true)

        val adapter = ChapterAdapter()
        binding.newChapterRecycler.adapter = adapter

        chapterViewModel = ViewModelProvider(this)[ChapterViewModel::class.java]
        lifecycleScope.launch {
            chapterViewModel.getNotUploadedChapters().collect {
                adapter.submitList(it)

                // show/hide background pun/help
                if (it.isNotEmpty()) {
                    val v = binding.newChapterBackground
                    v.visibility = View.GONE
                    // the following translation does not show as the view is gone, but is needed to animate the return
                    v.translationY = v.height.toFloat()
                } else {
                    val v = binding.newChapterBackground
                    v.animate().translationY(0F).withStartAction {
                        v.visibility = View.VISIBLE
                    }
                }
            }
        }

        adapter.setOnItemClickListener(this)
        adapter.setOnItemLongClickListener(this)

        return binding.root
    }

    //#region private functions

    /**
     * @param by 0 means by title, 1 by chapter
    // 0 - Manga title, then chapter number ASC
    // 1 - Manga title, then chapter number DESC
    // 2 - Chapter number ASC
    // 3 - Chapter number DESC
     */
    private fun setOrderOption(by: Int): Int {
        val pref = SharedPreferencesHandler(requireContext())
        when (pref.order) {
            0 -> {
                if (by == 0)
                    pref.order = 1
                else
                    pref.order = 2
            }
            1 -> {
                if (by == 0)
                    pref.order = 0
                else
                    pref.order = 2
            }
            2 -> {
                if (by == 1)
                    pref.order = 3
                else
                    pref.order = 0
            }
            3 -> {
                if (by == 1)
                    pref.order = 2
                else
                    pref.order = 0
            }
        }

        return pref.order
    }

//#endregion
//#region override functions

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.new_chapters_top_nav_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                Toast.makeText(context, "search!", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.action_sort_by_title -> {
                if (setOrderOption(0) == 1) {
                    item.setIcon(R.drawable.ic_baseline_text_rotation_up)
                } else {
                    item.setIcon(R.drawable.ic_baseline_text_rotation_down)
                }
                true
            }
            R.id.action_sort_by_chapter
            -> {
                if (setOrderOption(1) == 2) {
                    item.setIcon(R.drawable.ic_baseline_text_rotation_up)
                } else {
                    item.setIcon(R.drawable.ic_baseline_text_rotation_down)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

//#endregion
}