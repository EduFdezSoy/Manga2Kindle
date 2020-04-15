package es.edufdezsoy.manga2kindle.data.repository

import android.content.Context
import android.util.Log
import es.edufdezsoy.manga2kindle.M2kApplication
import es.edufdezsoy.manga2kindle.data.M2kDatabase
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.network.ApiService
import okhttp3.MultipartBody
import java.util.*
import kotlin.collections.ArrayList

class ChapterRepository {
    private val TAG = M2kApplication.TAG + "_ChapRepo"
    private val chapterList = ArrayList<Chapter>()
    private val database: M2kDatabase
    private val apiService = ApiService.apiService

    //#region object init

    companion object {
        @Volatile
        private var instance: ChapterRepository? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: ChapterRepository(context).also { instance = it }
        }
    }

    private constructor(context: Context) {
        this.database = M2kDatabase.invoke(context)
    }

    //#endregion
    //#region public methods

    suspend fun getAll(): ArrayList<Chapter> {
        if (chapterList.isEmpty()) {
            chapterList.addAll(database.ChapterDao().getAll())
        }

        return chapterList
    }

    suspend fun getChapter(identifier: Int): Chapter {
        chapterList.forEach {
            if (it.identifier == identifier)
                return it
        }

        val chap = database.ChapterDao().getChapter(identifier)
        chapterList.add(chap)

        return chap
    }

    suspend fun getNoUploadedChapters(): ArrayList<Chapter> {
        val coincidences = ArrayList<Chapter>()
        if (chapterList.isEmpty())
            getAll()

        if (chapterList.isNotEmpty()) {
            with(chapterList.iterator()) {
                forEach {
                    if (it.status == Chapter.STATUS_DEFAULT) {
                        coincidences.add(it)
                    }
                }
            }
        } else {
            coincidences.addAll(database.ChapterDao().getNoUploadedChapters())
        }

        return coincidences
    }

    suspend fun getUploadedChapters(): ArrayList<Chapter> {
        val coincidences = ArrayList<Chapter>()
        if (chapterList.isEmpty())
            getAll()

        if (chapterList.isNotEmpty()) {
            chapterList.forEach {
                if (it.status != Chapter.STATUS_DEFAULT)
                    coincidences.add(it)
            }
        } else {
            coincidences.addAll(database.ChapterDao().getUploadedChapters())
        }

        return coincidences
    }

    suspend fun getHiddenChapters(): ArrayList<Chapter> {
        val coincidences = ArrayList<Chapter>()
        if (chapterList.isEmpty())
            getAll()

        if (chapterList.isNotEmpty()) {
            chapterList.forEach {
                if (!it.visible)
                    coincidences.add(it)
            }
        } else {
            coincidences.addAll(database.ChapterDao().getHiddenChapters())
        }

        return coincidences
    }

    suspend fun getEnqueuedChapters(): ArrayList<Chapter> {
        val coincidences = ArrayList<Chapter>()
        if (chapterList.isEmpty())
            getAll()

        if (chapterList.isNotEmpty()) {
            with(chapterList.iterator()) {
                forEach {
                    if (it.enqueue_date != null && it.upload_date == null)
                        coincidences.add(it)
                }
            }
        } else {
            coincidences.addAll(database.ChapterDao().getEnqueuedChapters())
        }

        return coincidences
    }

    suspend fun search(manga_id: Int, chapter: Float): Chapter? {
        if (chapterList.isEmpty())
            chapterList.addAll(database.ChapterDao().getAll())

        if (chapterList.isNotEmpty()) {
            chapterList.forEach {
                if (it.manga_id == manga_id && it.chapter == chapter)
                    return it
            }
        }

        val chap = database.ChapterDao().search(manga_id, chapter)
        return chap
    }

    suspend fun insert(chapter: Chapter) {
        if (chapterList.isEmpty())
            chapterList.addAll(database.ChapterDao().getAll())

        val id = database.ChapterDao().insert(chapter)
        chapter.identifier = id.toInt()

        chapterList.add(chapter)
    }

    suspend fun update(chapter: Chapter) {
        if (chapterList.isEmpty())
            chapterList.addAll(database.ChapterDao().getAll())

        with(chapterList.iterator()) {
            forEach {
                if (it.identifier == chapter.identifier) {
                    remove()
                    return@forEach
                }
            }
        }.also {
            chapterList.add(chapter)
            database.ChapterDao().update(chapter)
        }
    }

    suspend fun enqueueUpload(chapter: Chapter) {
        chapter.enqueue_date = Calendar.getInstance().time
        chapter.status = Chapter.STATUS_ENQUEUE

        if (chapterList.isEmpty())
            chapterList.addAll(database.ChapterDao().getAll())

        with(chapterList.iterator()) {
            forEach {
                if (it.identifier == chapter.identifier) {
                    remove()
                    return@forEach
                }
            }
        }.also {
            chapterList.add(chapter)
            database.ChapterDao().update(chapter)
        }
    }

    suspend fun sendChapter(
        manga_id: Int,
        lang_id: Int,
        title: String,
        chapter: Float,
        volume: Int?,
        checksum: String,
        mail: String,
        options: String,
        file: MultipartBody.Part
    ): Chapter? {
        try {
            val list = apiService.sendChapter(
                manga_id,
                lang_id,
                title,
                chapter,
                volume,
                checksum,
                mail,
                options,
                file
            )

            if (list.isNotEmpty())
                return list[0]
            else
                return null

        } catch (e: Exception) {
            Log.e(TAG, "Something's bad with the upload!")
            e.printStackTrace()

            return null
        }
    }

    suspend fun hide(chapter: Chapter) = hide(chapter.identifier)
    suspend fun hide(identifier: Int) {
        with(chapterList.iterator()) {
            forEach {
                if (it.identifier == identifier) {
                    it.visible = false
                    return@forEach
                }
            }
        }
        database.ChapterDao().hide(identifier)
    }

    suspend fun delete(chapter: Chapter) {
        chapterList.remove(chapter)
        database.ChapterDao().delete(chapter)
    }

    suspend fun clearNotSended() {
        with(chapterList.iterator()) {
            forEach {
                if (it.status != Chapter.STATUS_UPLOADED) {
                    remove()
                }
            }
        }
        database.ChapterDao().clearNotSended()
    }

    //#endregion
}