package es.edufdezsoy.manga2kindle.ui.newChapters.chapterForm

import android.util.Log
import es.edufdezsoy.manga2kindle.M2kApplication
import es.edufdezsoy.manga2kindle.data.M2kDatabase
import es.edufdezsoy.manga2kindle.data.model.Author
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.data.model.Manga
import es.edufdezsoy.manga2kindle.network.ApiService

class ChapterFormInteractor(val controller: Controller, val database: M2kDatabase) {
    interface Controller {
        fun setManga(manga: Manga)
        fun setAuthor(author: Author)
        fun setMail(mail: String?)
        fun done()
    }

    suspend fun saveChapter(chapter: Chapter) {
        database.ChapterDao().update(chapter).also { controller.done() }
    }

    suspend fun getManga(id: Int) {
        database.MangaDao().getMangaById(id).also { controller.setManga(it) }
    }

    suspend fun saveManga(manga: Manga) {
        database.MangaDao().update(manga).also { controller.done() }
    }

    suspend fun getAuthor(id: Int) {
        var author = database.AuthorDao().getAuthor(id)

        if (author == null) {
            author = ApiService.apiService.getAuthor(id)[0]
            database.AuthorDao().insert(author)
        }

        controller.setAuthor(author)
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

    suspend fun getMail() {
        // TODO("get mail from shared preferences")
        controller.setMail(null)
    }

    suspend fun saveMail(mail: String) {
        // TODO("save mail into shared preferences")
    }

    suspend fun sendChapter(chapter: Chapter, mail: String) {
        chapter.sended = true
        database.ChapterDao().update(chapter)
        ApiService.apiService.sendChapter(
            manga_id = chapter.manga_id,
            lang_id = chapter.lang_id,
            title = chapter.title,
            chapter = chapter.chapter,
            volume = chapter.volume,
            checksum = chapter.checksum,
            mail = mail
        )
        controller.done()
    }
}