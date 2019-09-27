package es.edufdezsoy.manga2kindle.ui.newChapters.chapterForm

import android.app.Activity
import android.content.Context
import android.util.Log
import es.edufdezsoy.manga2kindle.M2kApplication
import es.edufdezsoy.manga2kindle.data.M2kDatabase
import es.edufdezsoy.manga2kindle.data.M2kSharedPref
import es.edufdezsoy.manga2kindle.data.model.Author
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.data.model.Manga
import es.edufdezsoy.manga2kindle.network.ApiService
import es.edufdezsoy.manga2kindle.service.UploadChapter

class ChapterFormInteractor(val controller: Controller, val database: M2kDatabase) {
    interface Controller {
        fun setManga(manga: Manga)
        fun setAuthor(author: Author)
        fun setAuthors(authors: List<Author>)
        fun setMail(mail: String?)
        fun done()
    }

    suspend fun saveChapter(chapter: Chapter) {
        database.ChapterDao().update(chapter)
    }

    suspend fun getManga(id: Int) {
        database.MangaDao().getMangaById(id).also { controller.setManga(it) }
    }

    suspend fun saveManga(manga: Manga) {
        var mangaOut = manga

        if (manga.author_id != null && !manga.synchronized)
            ApiService.apiService.addManga(manga.title, manga.author_id).also {
                it[0].synchronized = true
                it[0].identifier = manga.identifier

                mangaOut = it[0]
            }

        database.MangaDao().update(mangaOut)
    }

    suspend fun getAuthor(id: Int) {
        var author = database.AuthorDao().getAuthor(id)

        if (author == null) {
            author = ApiService.apiService.getAuthor(id)[0]
            database.AuthorDao().insert(author)
        }

        controller.setAuthor(author)
    }

    suspend fun getAuthors() {
        // TODO: this may get the authors from the database instead but whatever
        var authors = ApiService.apiService.getAllAuthors(null)
        database.AuthorDao().insert(*authors.toTypedArray())

        controller.setAuthors(authors)
    }

    suspend fun saveAuthor(author: Author) {
        if (database.AuthorDao().getAuthor(author.id) == null) {
            val authorFinal =
                ApiService.apiService.addAuthor(author.name, author.surname, author.nickname)[0]
            database.AuthorDao().insert(authorFinal)
        } else {
            Log.e(
                M2kApplication.TAG, "Author alredy exists"
                        + "\nID: " + author.id
                        + "\nName: " + author.name + " " + author.surname
                        + "\nAlias: " + author.nickname
            )
        }
    }

    suspend fun getMail(activity: Activity) {
        M2kSharedPref.invoke(activity).getString("mail", null).also {
            controller.setMail(it)
        }
    }

    suspend fun saveMail(activity: Activity, mail: String) {
        M2kSharedPref.invoke(activity).edit().putString("mail", mail).apply()
    }

    suspend fun sendChapter(chapter: Chapter, mail: String, context: Context) {
        UploadChapter(context).upload(chapter, mail)
        controller.done()
    }
}