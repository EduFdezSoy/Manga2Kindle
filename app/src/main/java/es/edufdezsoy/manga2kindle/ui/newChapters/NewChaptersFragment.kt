package es.edufdezsoy.manga2kindle.ui.newChapters

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.utils.MDUtil.ifNotZero
import es.edufdezsoy.manga2kindle.MainActivity
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.adapter.ChapterAdapter
import es.edufdezsoy.manga2kindle.adapter.ChapterBaseAdapter
import es.edufdezsoy.manga2kindle.adapter.ChapterCardAdapter
import es.edufdezsoy.manga2kindle.data.model.ChapterWithManga
import es.edufdezsoy.manga2kindle.data.model.UploadChapter
import es.edufdezsoy.manga2kindle.data.repository.SharedPreferencesHandler
import es.edufdezsoy.manga2kindle.databinding.FragmentNewChaptersBinding
import es.edufdezsoy.manga2kindle.service.ScanFoldersForMangaJobService
import es.edufdezsoy.manga2kindle.utils.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class NewChaptersFragment : Fragment(), ChapterBaseAdapter.OnItemClickListener,
    ChapterBaseAdapter.OnItemLongClickListener, ChapterCardAdapter.OnUploadItemListener,
    CoroutineScope, MenuProvider {
    private val TAG = this::class.java.simpleName
    private lateinit var chapterViewModel: ChapterViewModel
    private var scanning: Boolean = false

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewChaptersBinding.inflate(inflater, container, false)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.newChapterRecycler.layoutManager = LinearLayoutManager(context)
        binding.newChapterRecycler.setHasFixedSize(true)

        val adapter = ChapterAdapter()
        binding.newChapterRecycler.adapter = adapter

        chapterViewModel = ViewModelProvider(this)[ChapterViewModel::class.java]
        lifecycleScope.launch {
            chapterViewModel.getNotUploadedChapters().collectLatest {
                adapter.submitList(it)

                // TODO: this fails. It is only being done the first time the flow... flows
                // show/hide background pun/help
                if (adapter.itemCount > 0) {
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

        // set sync button bindings
        binding.ScanfloatingActionButton.setOnClickListener {
            binding.ScanfloatingActionButton.animate()
                .rotation(binding.ScanfloatingActionButton.rotation + 10 * 360f)
                .setDuration(10 * 1000).start()
            binding.ScanfloatingActionButton.isClickable = false
            startScheduledService()

            Toast.makeText(
                context,
                "Scanning, it may take a while (NOTE: the animation is not in sync with the scan)",
                Toast.LENGTH_LONG
            ).show()

            launch {
                delay(10 * 1000)
                binding.ScanfloatingActionButton.isClickable = true

            }
        }

        // move the button on start (as we always scan the lib when the app opens)
        lifecycleScope.launch {
            binding.ScanfloatingActionButton.animate().rotation(10 * 360f).setDuration(10 * 1000)
                .start()
            binding.ScanfloatingActionButton.isClickable = false

            delay(10 * 1000)
            binding.ScanfloatingActionButton.isClickable = true
        }

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
        // TODO: set icon position based on pref
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

    override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.new_chapters_top_nav_menu, menu)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
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

            else -> false
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
        (activity as MainActivity).uploadChapter(chapter)
    }

    //#endregion
//#region private functions
    private fun startScheduledService() {
        // textView.text = "scheduled service started"

        val executionTimer: Long = 15 * 60 * 1000 // 15 mins, the lower valid value
        val cn = ComponentName(requireContext(), ScanFoldersForMangaJobService::class.java)
        val ji = JobInfo.Builder(123, cn)
            .setRequiresCharging(false)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
            .setPersisted(true)
            .setPeriodic(executionTimer)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            // ji.setImportantWhileForeground(true) // this wont be needed for this job but may be usefull for the upload job
        }

        // check: https://developer.android.com/reference/android/app/job/JobInfo.Builder.html

        val scheduler = activity?.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val resultCode = scheduler.schedule(ji.build())
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "startScheduledService: Job Scheduled")
        } else {
            Log.d(TAG, "startScheduledService: Job Scheduling failed")
        }
    }
//#endregion
}