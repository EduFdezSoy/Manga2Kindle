package es.edufdezsoy.manga2kindle.ui.newChapters

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import es.edufdezsoy.manga2kindle.data.model.viewObject.NewChapter
import es.edufdezsoy.manga2kindle.data.repository.AuthorRepository
import es.edufdezsoy.manga2kindle.data.repository.ChapterRepository
import es.edufdezsoy.manga2kindle.data.repository.MangaRepository
import es.edufdezsoy.manga2kindle.service.intentService.ScanRemovedChaptersIntentService
import es.edufdezsoy.manga2kindle.service.util.BroadcastReceiver

class NewChaptersInteractor(val controller: Controller, context: Context) {
    interface Controller {
        fun setNewChapters(chapters: List<NewChapter>)
        fun updateList()
    }

    private val chapterRepository = ChapterRepository.invoke(context)
    private val mangaRepository = MangaRepository.invoke(context)
    private val authorRepository = AuthorRepository.invoke(context)
    private val receiver: BroadcastReceiver

    init {
        // register receiver
        val filter = IntentFilter(BroadcastReceiver.ACTION_UPDATED_CHAPTER_LIST)
        filter.addCategory(Intent.CATEGORY_DEFAULT)
        receiver = BroadcastReceiver(BroadcastReceiver.ACTION_UPDATED_CHAPTER_LIST) {
            controller.updateList()
        }
        context.registerReceiver(receiver, filter)
    }

    suspend fun loadChapters() {
        getChaptersList().also {
            controller.setNewChapters(it)
        }
    }

    suspend fun updateChapters(context: Context) {
        val intent = Intent()
        ScanRemovedChaptersIntentService.enqueueWork(context, intent)
    }

    fun close(context: Context) {
        context.unregisterReceiver(receiver)
    }

    private suspend fun getChaptersList(): ArrayList<NewChapter> {
        chapterRepository.getNoUploadedChapters().also {
            val newChapters = ArrayList<NewChapter>()
            it.forEach {
                val manga = mangaRepository.getMangaById(it.manga_id)

                var author = ""
                if (manga.author_id != null) {
                    val au = authorRepository.getAuthor(manga.author_id)
                    if (au != null)
                        author = au.toString()
                }

                newChapters.add(
                    NewChapter(
                        local_id = it.identifier,
                        chapter = it.toString(),
                        manga_id = manga.id,
                        manga_local_id = manga.identifier,
                        manga_title = manga.title,
                        author_id = manga.author_id,
                        author = author
                    )
                )
            }

            return newChapters
        }
    }
}