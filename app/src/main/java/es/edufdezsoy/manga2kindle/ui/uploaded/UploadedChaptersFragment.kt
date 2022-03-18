package es.edufdezsoy.manga2kindle.ui.uploaded

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import es.edufdezsoy.manga2kindle.adapter.ChapterBaseAdapter
import es.edufdezsoy.manga2kindle.adapter.ChapterCardAdapter
import es.edufdezsoy.manga2kindle.adapter.UploadedChapterAdapter
import es.edufdezsoy.manga2kindle.data.model.ChapterWithManga
import es.edufdezsoy.manga2kindle.data.model.UploadChapter
import es.edufdezsoy.manga2kindle.data.repository.SharedPreferencesHandler
import es.edufdezsoy.manga2kindle.databinding.FragmentUploadedChaptersBinding
import es.edufdezsoy.manga2kindle.ui.newChapters.ChapterViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class UploadedChaptersFragment : Fragment(), ChapterBaseAdapter.OnItemClickListener,
    ChapterBaseAdapter.OnItemLongClickListener, ChapterCardAdapter.OnUploadItemListener {
    private lateinit var chapterViewModel: ChapterViewModel
    private lateinit var statusViewModel: StatusViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentUploadedChaptersBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.uploadedChapterRecycler.layoutManager
        binding.uploadedChapterRecycler.layoutManager = LinearLayoutManager(context)
        binding.uploadedChapterRecycler.setHasFixedSize(true)

        val adapter = UploadedChapterAdapter()
        binding.uploadedChapterRecycler.adapter = adapter

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
        uploadChapter.email = SharedPreferencesHandler(requireContext()).kindleEmail
    }
}