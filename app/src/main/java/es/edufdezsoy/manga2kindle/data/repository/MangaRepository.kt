package es.edufdezsoy.manga2kindle.data.repository

import android.app.Application
import es.edufdezsoy.manga2kindle.data.M2KDatabase
import es.edufdezsoy.manga2kindle.data.dao.MangaDao
import es.edufdezsoy.manga2kindle.data.model.Manga
import es.edufdezsoy.manga2kindle.network.ApiService
import es.edufdezsoy.manga2kindle.network.Manga2KindleService

class MangaRepository(application: Application) {
    private val mangaDao: MangaDao
    private val apiService: Manga2KindleService

    init {
        val database = M2KDatabase.getInstance(application.applicationContext)
        mangaDao = database.mangaDao()
        apiService = ApiService.getInstance(application.applicationContext)
    }

    suspend fun insert(manga: Manga): Manga {
        return mangaDao.insert(manga)
    }

    suspend fun update(manga: Manga): Manga {
        return mangaDao.update(manga)
    }

    suspend fun delete(manga: Manga) {
        mangaDao.delete(manga)
    }

    suspend fun search(title: String): Manga? {
        return mangaDao.search(title)
    }

    suspend fun searchOrCreate(title: String): Manga {
        // search local
        val manga = mangaDao.search(title)
        if (manga != null) {
            return manga
        }

        // search online (and insert in local if exists)
        val mangas = apiService.searchManga(title)
        if (mangas[0] != null) {
            return insert(mangas[0]!!)
        }

        // create local
        return insert(Manga(title))
    }
}