package es.edufdezsoy.manga2kindle.ui.uploadedChapters

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.M2kDatabase
import es.edufdezsoy.manga2kindle.data.model.viewObject.UploadedChapter
import es.edufdezsoy.manga2kindle.service.intentService.UpdateChapterStatusIntentService
import es.edufdezsoy.manga2kindle.service.util.BroadcastReceiver

class UploadedChaptersInteractor(val controller: Controller, val database: M2kDatabase) {
    interface Controller {
        fun setNewChapters(chapters: List<UploadedChapter>)
        fun updateList()
    }

    private lateinit var receiver: BroadcastReceiver

    suspend fun loadChapters() {
        getChaptersList().also {
            controller.setNewChapters(it)
        }
    }

    suspend fun updateStatus(context: Context) {
        UpdateChapterStatusIntentService.enqueueWork(context, Intent())

        // register reciver
        if (!::receiver.isInitialized) {
            val filter = IntentFilter(BroadcastReceiver.ACTION_UPDATED_CHAPTER_STATUS)
            filter.addCategory(Intent.CATEGORY_DEFAULT)
            receiver = BroadcastReceiver(BroadcastReceiver.ACTION_UPDATED_CHAPTER_STATUS) {
                controller.updateList()
            }
            context.registerReceiver(receiver, filter)
        }
    }

    fun close(context: Context) {
        if (::receiver.isInitialized)
            context.unregisterReceiver(receiver)
    }

    private suspend fun getChaptersList(): ArrayList<UploadedChapter> {
        database.ChapterDao().getUploadedChapters().also {
            val uploadedChapters = ArrayList<UploadedChapter>()
            it.forEach {
                val manga = database.MangaDao().getMangaById(it.manga_id)

                var author = ""
                if (manga.author_id != null) {
                    val au = database.AuthorDao().getAuthor(manga.author_id)
                    if (au != null)
                        author = au.toString()
                }

                val status: String
                val status_color: Int
                var reason = ""
                if (!it.error) {
                    if (!it.delivered) {
                        status = "processing"
                        status_color = R.color.colorProcessing
                    } else {
                        status = "success"
                        status_color = R.color.colorSuccess
                    }
                } else {
                    status = "failed"
                    status_color = R.color.colorFailed
                    reason = it.reason.toString()
                }

                uploadedChapters.add(
                    UploadedChapter(
                        it.id!!,
                        it.identifier,
                        it.toString(),
                        it.manga_id,
                        manga.title,
                        manga.author_id,
                        author,
                        status,
                        status_color,
                        reason
                    )
                )
            }

            return uploadedChapters
        }
    }
}