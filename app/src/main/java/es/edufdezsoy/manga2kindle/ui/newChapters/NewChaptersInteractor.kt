package es.edufdezsoy.manga2kindle.ui.newChapters

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import es.edufdezsoy.manga2kindle.data.M2kDatabase
import es.edufdezsoy.manga2kindle.data.model.viewObject.NewChapter
import es.edufdezsoy.manga2kindle.service.ScanRemovedChapters

class NewChaptersInteractor(val controller: Controller, val database: M2kDatabase) {
    interface Controller {
        fun setNewChapters(chapters: List<NewChapter>)
        fun updateList()
    }

    private lateinit var receiver: ServiceReceiver

    suspend fun loadChapters() {
        getChaptersList().also {
            controller.setNewChapters(it)
        }
    }

    suspend fun updateChapters(context: Context) {
        ScanRemovedChapters.enqueueWork(context, Intent())

        // register receiver
        if (!::receiver.isInitialized) {
            val filter = IntentFilter(ServiceReceiver.ACTION_UPDATED_CHAPTER_LIST)
            filter.addCategory(Intent.CATEGORY_DEFAULT)
            receiver = ServiceReceiver(controller)
            context.registerReceiver(receiver, filter)
        }
    }

    fun close(context: Context) {
        if (::receiver.isInitialized)
            context.unregisterReceiver(receiver)
    }

    private suspend fun getChaptersList(): ArrayList<NewChapter> {
        database.ChapterDao().getNoUploadedChapters().also {
            val newChapters = ArrayList<NewChapter>()
            it.forEach {
                val manga = database.MangaDao().getMangaById(it.manga_id)

                var author = ""
                if (manga.author_id != null) {
                    val au = database.AuthorDao().getAuthor(manga.author_id)
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

    class ServiceReceiver(val controller: Controller) : BroadcastReceiver() {

        companion object {
            val ACTION_UPDATED_CHAPTER_LIST = "es.edufdezsoy.intent.action.UPDATED_CHAPTER_LIST"
        }

        override fun onReceive(context: Context?, intent: Intent?) {
            controller.updateList()
        }
    }
}