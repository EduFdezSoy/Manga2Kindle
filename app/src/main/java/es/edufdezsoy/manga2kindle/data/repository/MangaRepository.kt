package es.edufdezsoy.manga2kindle.data.repository

import android.app.Application
import android.util.Log
import es.edufdezsoy.manga2kindle.data.M2KDatabase
import es.edufdezsoy.manga2kindle.data.dao.MangaDao
import es.edufdezsoy.manga2kindle.data.model.Manga
import es.edufdezsoy.manga2kindle.network.ApiService
import es.edufdezsoy.manga2kindle.network.Manga2KindleService
import es.edufdezsoy.manga2kindle.network.adapters.PrepareQueryParamsAdapter
import kotlin.reflect.typeOf

class MangaRepository(application: Application) {
    private val mangaDao: MangaDao
    private val apiService: Manga2KindleService

    init {
        val database = M2KDatabase.getInstance(application.applicationContext)
        mangaDao = database.mangaDao()
        apiService = ApiService.getInstance(application.applicationContext)
    }

    fun insert(manga: Manga): Manga {
        val id = mangaDao.insert(manga)
        return mangaDao.get(id)
    }

    fun update(manga: Manga) {
        mangaDao.update(manga)
    }

    fun delete(manga: Manga) {
        mangaDao.delete(manga)
    }

    fun get(id: Int): Manga {
        return mangaDao.get(id.toLong())
    }

    fun search(title: String): Manga? {
        return mangaDao.search(title)
    }

    suspend fun searchOrCreate(title: String): Manga {
        // search local
        val manga = mangaDao.search(title)
        if (manga != null) {
            return manga
        }

        // search online (and insert in local if exists)
        val mangas = apiService.searchManga(PrepareQueryParamsAdapter("title", title).toString())
        if (mangas.items.isNotEmpty()) {
            Log.d("MangaRepository", "searchOrCreate: ${mangas.javaClass.name}")
            return insert(mangas.items[0])
        }

        // create local
        return insert(Manga(title))
    }
}