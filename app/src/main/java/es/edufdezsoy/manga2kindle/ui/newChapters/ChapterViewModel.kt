package es.edufdezsoy.manga2kindle.ui.newChapters

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.data.model.ChapterWithManga
import es.edufdezsoy.manga2kindle.data.repository.ChapterRepository
import es.edufdezsoy.manga2kindle.data.repository.MangaRepository
import es.edufdezsoy.manga2kindle.data.repository.SharedPreferencesHandler
import es.edufdezsoy.manga2kindle.utils.ChapterWithMangaComparator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ChapterViewModel(application: Application) : AndroidViewModel(application) {
    private val chapterRepository = ChapterRepository(application)
    private val mangaRepository = MangaRepository(application)
    private lateinit var chapters: Flow<List<ChapterWithManga>>
    private val pref = SharedPreferencesHandler(application)

    init {
        viewModelScope.launch {
            chapters = chapterRepository.getAllChapters().map {
                val list = ArrayList<ChapterWithManga>()
                it.forEach {
                    list.add(ChapterWithManga(it, mangaRepository.get(it.mangaId)))
                }

                // 0 - Manga title, then chapter number ASC
                // 1 - Manga title, then chapter number DESC
                // 2 - Chapter number ASC
                // 3 - Chapter number DESC
                when (pref.order) {
                    0 -> {
                        list.sortWith(ChapterWithMangaComparator.DefaultComparator())
                    }
                    1 -> {
                        list.sortWith(ChapterWithMangaComparator.DefaultComparator())
                        list.reverse()
                    }
                    2 -> {
                        list.sortWith(ChapterWithMangaComparator.ChapterComparator())
                    }
                    3 -> {
                        list.sortWith(ChapterWithMangaComparator.ChapterComparator())
                        list.reverse()
                    }
                }

                return@map list
            }
        }
    }

    fun insert(chapter: Chapter) {
        viewModelScope.launch {
            chapterRepository.insert(chapter)
        }
    }

    fun update(chapter: Chapter) {
        viewModelScope.launch {
            chapterRepository.update(chapter)
        }
    }

    fun delete(chapter: Chapter) {
        viewModelScope.launch {
            chapterRepository.delete(chapter)
        }
    }

    fun deleteAllChapters() {
        viewModelScope.launch {
            chapterRepository.deleteAllChapters()
        }
    }

    fun getAllChapters(): Flow<List<ChapterWithManga>> {
        return chapters
    }

    fun getNotUploadedChapters(): Flow<List<ChapterWithManga>> {
        return chapters.map { it.filter { item -> item.chapter.status == 0 } }
    }

    fun getUploadedChapters(): Flow<List<ChapterWithManga>> {
        return chapters.map { it.filterNot { item -> item.chapter.status == 0 } }
    }
}