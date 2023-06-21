package es.edufdezsoy.manga2kindle.ui.watchedFolders

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import es.edufdezsoy.manga2kindle.adapter.FolderAdapter
import es.edufdezsoy.manga2kindle.adapter.FolderCardAdapter
import es.edufdezsoy.manga2kindle.data.model.Folder
import es.edufdezsoy.manga2kindle.databinding.FragmentWatchedFoldersBinding
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
}