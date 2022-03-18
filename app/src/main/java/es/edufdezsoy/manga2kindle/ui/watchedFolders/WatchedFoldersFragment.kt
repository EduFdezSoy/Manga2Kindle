package es.edufdezsoy.manga2kindle.ui.watchedFolders

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context.JOB_SCHEDULER_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import es.edufdezsoy.manga2kindle.adapter.FolderAdapter
import es.edufdezsoy.manga2kindle.adapter.FolderCardAdapter
import es.edufdezsoy.manga2kindle.data.model.Folder
import es.edufdezsoy.manga2kindle.databinding.FragmentWatchedFoldersBinding
import es.edufdezsoy.manga2kindle.service.ExampleService
import es.edufdezsoy.manga2kindle.service.ScanFoldersForMangaJobService
import es.edufdezsoy.manga2kindle.utils.Log
import kotlinx.coroutines.launch


class WatchedFoldersFragment : Fragment(), FolderAdapter.OnItemClickListener,
    FolderAdapter.OnItemLongClickListener {
    private val TAG = this::class.java.simpleName
    private val PICK_FOLDER_REQUEST_CODE = 1

    private lateinit var folderViewModel: FolderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding = FragmentWatchedFoldersBinding.inflate(inflater, container, false)

        binding.folderListRecycler.layoutManager = LinearLayoutManager(context)
        binding.folderListRecycler.setHasFixedSize(true)

        val adapter = FolderAdapter()
        binding.folderListRecycler.adapter = adapter

        folderViewModel = ViewModelProvider(this)[FolderViewModel::class.java]
        lifecycleScope.launch {
            folderViewModel.getAllFolders().observe(viewLifecycleOwner) {
                adapter.submitList(it)

                // show/hide background pun/help
                if (it.isNotEmpty()) {
                    val v = binding.watchedFoldersBackground
                    v.visibility = View.GONE
                    // the following translation does not show as the view is gone, but is needed to animate the return
                    v.translationY = v.height.toFloat()
                } else {
                    val v = binding.watchedFoldersBackground
                    v.animate().translationY(0F).withStartAction {
                        v.visibility = View.VISIBLE
                    }
                }
            }
        }

        // set listeners
        adapter.setOnItemClickListener(this)
        adapter.setOnItemLongClickListener(this)
        binding.floatingActionButton.setOnClickListener { performFileSearch() }
        binding.watchedFoldersBackgroundHelpTextLayout.setOnClickListener { openHelpDialog() }

        binding.buttonLaunchService.setOnClickListener {
            // launchScanService()
            startScheduledService()
            Toast.makeText(
                context,
                "ya voooy... no pulses el boton en un rato que igual rompes otra cosa",
                Toast.LENGTH_LONG
            ).show()
        }

        return binding.root
    }

    private fun openHelpDialog() {
        WatchedFoldersHelpDialog(
            requireContext(),
            viewLifecycleOwner,
            requireActivity().supportFragmentManager
        ).show()
    }

    private fun performFileSearch() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            .addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            .addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
            .addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        startActivityForResult(intent, PICK_FOLDER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_FOLDER_REQUEST_CODE && data != null) {
            val path = data.data.toString()
            val readablePath = Uri.parse(path).path!!
            var name = readablePath.substring(readablePath.lastIndexOf('/') + 1)
            name = name.substring(name.lastIndexOf(':') + 1)

            folderViewModel.insert(Folder(name, path, true))

            // get permissions on sub-folders and files (read and write)
            requireContext().contentResolver.takePersistableUriPermission(
                data.data!!,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            requireContext().contentResolver.takePersistableUriPermission(
                data.data!!,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        }
    }

    override fun onItemClick(folder: Folder) {
        folder.active = !folder.active
        folderViewModel.update(folder)
    }

    override fun onItemLongClick(folder: Folder) {
        val folderCard = FolderCardAdapter(requireContext(), viewLifecycleOwner)
        folderCard.setFolder(folder, folderViewModel)
        folderCard.show()
    }

    // TODO: service shit that may be removed

    private fun launchScanService() {
        val serviceIntent = Intent(context, ScanFoldersForMangaJobService::class.java)
        activity?.startService(serviceIntent)
    }

    private fun startService() {
        // textView.text = "service started"

        val serviceIntent = Intent(context, ExampleService::class.java)
        serviceIntent.putExtra("inputExtra", "example input")

        activity?.startService(serviceIntent)
    }

    private fun stopService() {
        // textView.text = "service stopped"

        val serviceIntent = Intent(context, ExampleService::class.java)
        activity?.stopService(serviceIntent)
    }

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

        val scheduler = activity?.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        val resultCode = scheduler.schedule(ji.build())
        if (resultCode == JobScheduler.RESULT_SUCCESS)
            Log.d(TAG, "startScheduledService: Job Scheduled")
        else
            Log.d(TAG, "startScheduledService: Job Scheduling failed")
    }

    private fun stopScheduledService() {
        // textView.text = "scheduled service stoped"
        val scheduler = activity?.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        scheduler.cancel(123)
        Log.d(TAG, "stopScheduledService: Job Canceled")
    }
}