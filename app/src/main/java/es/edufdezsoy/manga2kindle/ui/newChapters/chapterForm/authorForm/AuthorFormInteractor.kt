package es.edufdezsoy.manga2kindle.ui.newChapters.chapterForm.authorForm

import es.edufdezsoy.manga2kindle.data.M2kDatabase
import es.edufdezsoy.manga2kindle.data.model.Author
import es.edufdezsoy.manga2kindle.data.model.Manga
import es.edufdezsoy.manga2kindle.network.ApiService


class AuthorFormInteractor(val controller: Controller, val database: M2kDatabase) {
    interface Controller {
        fun setAuthorList(authors: List<Author>)
        fun setAuthor(author: Author)
        fun setManga(manga: Manga)
        fun done()
    }

    suspend fun getAuthors(str: String) {
        var author = database.AuthorDao().search(str)

        if (author.isEmpty()) {
            author = ApiService.apiService.searchAuthor(str)
            database.AuthorDao().insert(*author.toTypedArray())
        }

        controller.setAuthorList(author)
    }

    suspend fun getAuthor(id: Int) {
        var author = database.AuthorDao().getAuthor(id)

        if (author == null) {
            author = ApiService.apiService.getAuthor(id)[0]
            database.AuthorDao().insert(author)
        }

        controller.setAuthor(author)
    }

    suspend fun saveAuthor(name: String, surname: String, nickname: String) {
        ApiService.apiService.addAuthor(name, surname, nickname).also {
            database.AuthorDao().insert(it[0])
        }

        controller.done()
    }

    suspend fun getManga(id: Int) {
        database.MangaDao().getMangaById(id).also {
            controller.setManga(it)
        }
    }
}