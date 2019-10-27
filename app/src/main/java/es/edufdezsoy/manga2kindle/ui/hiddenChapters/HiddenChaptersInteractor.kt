package es.edufdezsoy.manga2kindle.ui.hiddenChapters

import android.content.Context
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.data.model.viewObject.HiddenChapter
import es.edufdezsoy.manga2kindle.data.repository.AuthorRepository
import es.edufdezsoy.manga2kindle.data.repository.ChapterRepository
import es.edufdezsoy.manga2kindle.data.repository.MangaRepository

class HiddenChaptersInteractor(val controller: Controller, context: Context) {
    interface Controller {
        fun setChapters(chapters: List<HiddenChapter>)
        fun loadChapters()
    }

    private val chapterRepository = ChapterRepository.invoke(context)
    private val mangaRepository = MangaRepository.invoke(context)
    private val authorRepository = AuthorRepository.invoke(context)


    suspend fun loadChapters() {
        getChaptersList().also {
            controller.setChapters(it)
        }
    }

    suspend fun showChapter(chapter: HiddenChapter) {
        val chap = chapterRepository.getChapter(chapter.local_id)
        chap.visible = true
        chapterRepository.update(chap).also {
           controller.loadChapters()
        }
    }

    suspend fun hideChapter(chapter: HiddenChapter) {
        chapterRepository.hide(chapter.local_id).also {
            controller.loadChapters()
        }
    }

    private suspend fun getChaptersList(): ArrayList<HiddenChapter> {
        chapterRepository.getHiddenChapters().also {
            val hiddenChapters = ArrayList<HiddenChapter>()

            it.forEach {
                val manga = mangaRepository.getMangaById(it.manga_id)

                var author = ""
                if (manga.author_id != null) {
                    val au = authorRepository.getAuthor(manga.author_id)
                    if (au != null)
                        author = au.toString()
                }

                var status: String? = null
                var status_color: Int? = null
                var reason: String? = null

                when (it.status) {
                    Chapter.STATUS_ENQUEUE -> {
                        status = "enqueue"
                        status_color = R.color.colorEnqueue
                    }
                    Chapter.STATUS_PROCESSING -> {
                        status = "compressing"
                        status_color = R.color.colorCompressing
                    }
                    Chapter.STATUS_UPLOADING -> {
                        status = "uploading"
                        status_color = R.color.colorUploading
                    }
                    Chapter.STATUS_UPLOADED -> {
                        status = "processing"
                        status_color = R.color.colorProcessing
                    }
                    Chapter.STATUS_LOCAL_ERROR -> {
                        status = "failed at home"
                        status_color = R.color.colorFailed
                    }
                }

                if (it.error) {
                    status = "failed"
                    status_color = R.color.colorFailed
                    reason = it.reason.toString()
                } else if (it.delivered) {
                    status = "success"
                    status_color = R.color.colorSuccess
                }

                hiddenChapters.add(
                    HiddenChapter(
                        it.id,
                        it.identifier,
                        it.toString(),
                        it.manga_id,
                        manga.title,
                        manga.author_id,
                        author,
                        it.status,
                        status,
                        status_color,
                        reason,
                        it.upload_date
                    )
                )
            }

            return hiddenChapters
        }
    }
}