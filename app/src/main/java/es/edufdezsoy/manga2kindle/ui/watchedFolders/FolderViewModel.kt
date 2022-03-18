package es.edufdezsoy.manga2kindle.ui.watchedFolders

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import es.edufdezsoy.manga2kindle.data.model.Folder
import es.edufdezsoy.manga2kindle.data.repository.FolderRepository
import kotlinx.coroutines.launch

class FolderViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FolderRepository(application)
    private lateinit var folders: LiveData<List<Folder>>

    init {
        viewModelScope.launch {
            folders = repository.getAllFolders()
        }
    }

    fun insert(folder: Folder) {
        viewModelScope.launch {
            repository.insert(folder)
        }
    }

    fun update(folder: Folder) {
        viewModelScope.launch {
            repository.update(folder)
        }
    }

    fun delete(folder: Folder) {
        viewModelScope.launch {
            repository.delete(folder)
        }
    }

    fun getAllFolders(): LiveData<List<Folder>> {
        return folders
    }
}