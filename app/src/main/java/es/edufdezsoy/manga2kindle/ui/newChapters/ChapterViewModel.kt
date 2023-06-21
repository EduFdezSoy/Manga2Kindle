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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ChapterViewModel(application: Application) : AndroidViewModel(application) {
    private val chapterRepository = ChapterRepository(application)
    private val mangaRepository = MangaRepository(application)
    private val pref = SharedPreferencesHandler(application)


    fun insert(chapter: Chapter) {
        viewModelScope.launch(Dispatchers.IO) {
            chapterRepository.insert(chapter)
        }
    }

    fun update(chapter: Chapter) {
        viewModelScope.launch(Dispatchers.IO) {
            chapterRepository.update(chapter)
        }
    }

    fun delete(chapter: Chapter) {
        viewModelScope.launch(Dispatchers.IO) {
            chapterRepository.delete(chapter)
        }
    }

    fun deleteAllChapters() {
        viewModelScope.launch(Dispatchers.IO) {
            chapterRepository.deleteAllChapters()
        }
    }

    fun getNotUploadedChapters(): Flow<List<ChapterWithManga>> {
        return chapterRepository.getAllChapters().map {
            val list = ArrayList<ChapterWithManga>()
            it.filter { item -> item.status.isEmpty() }.forEach {
                viewModelScope.launch(Dispatchers.IO) {
                    list.add(ChapterWithManga(it, mangaRepository.get(it.mangaId)))

                    // TODO: how the f do I do the sort without ConcurrentModificationException AND after the coroutine (since before there is no data to sort)
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
                }
            }

            return@map list
        }
    }

    fun getUploadedChapters(): Flow<List<ChapterWithManga>> {
        return chapterRepository.getAllChapters().map {
            val list = ArrayList<ChapterWithManga>()
            it.filterNot { item -> item.status.isEmpty() }.forEach {
                viewModelScope.launch(Dispatchers.IO) {
                    list.add(ChapterWithManga(it, mangaRepository.get(it.mangaId)))

                    // TODO: this order is not what we want here, we should order this list by upload date desc
                    list.sortWith(ChapterWithMangaComparator.DefaultComparator())
                }
            }

            return@map list
        }
    }
}