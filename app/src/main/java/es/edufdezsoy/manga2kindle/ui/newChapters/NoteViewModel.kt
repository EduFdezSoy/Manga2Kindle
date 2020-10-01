package es.edufdezsoy.manga2kindle.ui.newChapters

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.data.repository.ChapterRepository
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ChapterRepository(application)
    private lateinit var notes: LiveData<List<Chapter>>

    init {
        viewModelScope.launch {
            notes = repository.getAllNotes()
        }
    }

    fun insert(chapter: Chapter) {
        viewModelScope.launch {
            repository.insert(chapter)
        }
    }

    fun update(chapter: Chapter) {
        viewModelScope.launch {
            repository.update(chapter)
        }
    }

    fun delete(chapter: Chapter) {
        viewModelScope.launch {
            repository.delete(chapter)
        }
    }

    fun deleteAllNotes() {
        viewModelScope.launch {
            repository.deleteAllNotes()
        }
    }


    fun getAllNotes(): LiveData<List<Chapter>> {
        return notes
    }
}