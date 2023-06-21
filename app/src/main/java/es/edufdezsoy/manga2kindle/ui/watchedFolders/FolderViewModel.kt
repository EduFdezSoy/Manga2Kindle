package es.edufdezsoy.manga2kindle.ui.watchedFolders

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import es.edufdezsoy.manga2kindle.data.model.Folder
import es.edufdezsoy.manga2kindle.data.repository.FolderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FolderViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FolderRepository(application)

    fun insert(folder: Folder) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(folder)
        }
    }

    fun update(folder: Folder) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(folder)
        }
    }

    fun delete(folder: Folder) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(folder)
        }
    }

    fun getAllFolders(): LiveData<List<Folder>> {
        return repository.getAllFolders()
    }
}