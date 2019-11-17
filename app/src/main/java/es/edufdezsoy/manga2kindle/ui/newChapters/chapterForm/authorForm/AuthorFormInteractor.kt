package es.edufdezsoy.manga2kindle.ui.newChapters.chapterForm.authorForm

import android.content.Context
import es.edufdezsoy.manga2kindle.data.model.Author
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.data.model.Manga
import es.edufdezsoy.manga2kindle.data.repository.AuthorRepository
import es.edufdezsoy.manga2kindle.data.repository.ChapterRepository
import es.edufdezsoy.manga2kindle.data.repository.MangaRepository


class AuthorFormInteractor(val controller: Controller, context: Context) {
    interface Controller {
        fun setAuthorList(authors: List<Author>)
        fun setAuthor(author: Author)
        fun setManga(manga: Manga)
        fun setChapter(chapter: Chapter)
        fun done()
    }

    private val chapterRepository = ChapterRepository.invoke(context)
    private val mangaRepository = MangaRepository.invoke(context)
    private val authorRepository = AuthorRepository.invoke(context)

    suspend fun getAuthors(str: String) {
        authorRepository.search(str).also { controller.setAuthorList(it) }
    }

    suspend fun getAuthor(id: Int) {
        authorRepository.getAuthor(id).also {
            if (it != null)
                controller.setAuthor(it)
        }
    }

    suspend fun saveAuthor(name: String, surname: String, nickname: String) {
        authorRepository.insert(name, surname, nickname)
        controller.done()
    }

    suspend fun getManga(id: Int) {
        mangaRepository.getMangaById(id).also {
            controller.setManga(it)
        }
    }

    suspend fun getChapter(id: Int) {
        chapterRepository.getChapter(id).also {
            controller.setChapter(it)
        }
    }
}