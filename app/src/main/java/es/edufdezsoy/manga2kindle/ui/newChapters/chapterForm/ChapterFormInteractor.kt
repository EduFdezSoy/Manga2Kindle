package es.edufdezsoy.manga2kindle.ui.newChapters.chapterForm

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import es.edufdezsoy.manga2kindle.M2kApplication
import es.edufdezsoy.manga2kindle.data.M2kSharedPref
import es.edufdezsoy.manga2kindle.data.model.Author
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.data.model.Manga
import es.edufdezsoy.manga2kindle.data.repository.AuthorRepository
import es.edufdezsoy.manga2kindle.data.repository.ChapterRepository
import es.edufdezsoy.manga2kindle.data.repository.MangaRepository
import es.edufdezsoy.manga2kindle.service.UploadChapter
import es.edufdezsoy.manga2kindle.service.intentService.UploadChapterIntentService
import es.edufdezsoy.manga2kindle.service.util.BroadcastReceiver

class ChapterFormInteractor(val controller: Controller, context: Context) {
    interface Controller {
        fun setChapter(chapter: Chapter)
        fun setManga(manga: Manga)
        fun setAuthor(author: Author)
        fun setAuthors(authors: List<Author>)
        fun setMail(mail: String?)
        fun done()
    }

    private lateinit var receiver: BroadcastReceiver
    private val chapterRepository = ChapterRepository.invoke(context)
    private val mangaRepository = MangaRepository.invoke(context)
    private val authorRepository = AuthorRepository.invoke(context)

    suspend fun getChapter(chapter_id: Int) {
        chapterRepository.getChapter(chapter_id).also {
            controller.setChapter(it)
        }
    }

    suspend fun saveChapter(chapter: Chapter) {
        chapterRepository.update(chapter)
    }

    suspend fun getManga(id: Int) {
        mangaRepository.getMangaById(id).also { controller.setManga(it) }
    }

    suspend fun saveManga(manga: Manga) {
        mangaRepository.update(manga)
    }

    suspend fun getAuthor(id: Int) {
        authorRepository.getAuthor(id).also {
            if (it != null)
                controller.setAuthor(it)
        }
    }

    suspend fun getAuthors() {
        authorRepository.getAll().also { controller.setAuthors(it) }
    }

    suspend fun saveAuthor(author: Author) {
        if (authorRepository.getAuthor(author.id) == null) {
            authorRepository.insert(author)
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

    suspend fun sendChapter(chapter_id: Int, mail: String, context: Context) {
        val intent = Intent()
        intent.putExtra(UploadChapter.CHAPTER_ID_KEY, chapter_id)
        intent.putExtra(UploadChapter.MAIL_KEY, mail)

        UploadChapterIntentService.enqueueWork(context, intent)

        if (!::receiver.isInitialized) {
            val filter = IntentFilter(BroadcastReceiver.ACTION_UPLOADED_CHAPTER)
            filter.addCategory(Intent.CATEGORY_DEFAULT)
            receiver = BroadcastReceiver(BroadcastReceiver.ACTION_UPLOADED_CHAPTER) {
                controller.done()
            }
            context.registerReceiver(receiver, filter)
        }
    }

    fun close(context: Context) {
        if (::receiver.isInitialized)
            context.unregisterReceiver(receiver)
    }
}