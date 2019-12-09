package es.edufdezsoy.manga2kindle.data.repository

import android.content.Context
import android.util.Log
import es.edufdezsoy.manga2kindle.M2kApplication
import es.edufdezsoy.manga2kindle.data.M2kDatabase
import es.edufdezsoy.manga2kindle.data.model.Manga
import es.edufdezsoy.manga2kindle.network.ApiService

class MangaRepository {
    private val TAG = M2kApplication.TAG + "_MangaRepo"
    private val mangaList = ArrayList<Manga>()
    private val database: M2kDatabase
    private val apiService = ApiService.apiService

    //#region object init

    companion object {
        @Volatile
        private var instance: MangaRepository? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: MangaRepository(context).also { instance = it }
        }
    }

    private constructor(context: Context) {
        this.database = M2kDatabase.invoke(context)
    }

    //#endregion
    //#region public methods

    suspend fun getAll(): ArrayList<Manga> {
        if (mangaList.isEmpty())
            mangaList.addAll(database.MangaDao().getAll())

        return mangaList
    }

    suspend fun getManga(id: Int): Manga {
        mangaList.forEach {
            if (it.id == id)
                return it
        }

        val manga = database.MangaDao().getManga(id)
        mangaList.add(manga)
        return manga
    }

    suspend fun getMangaById(identifier: Int): Manga {
        with(mangaList.iterator()) {
            forEach {
                if (it.identifier == identifier) {
                    return@getMangaById it
                }
            }
        }

        val manga = database.MangaDao().getMangaById(identifier)
        mangaList.add(manga)
        return manga
    }

    suspend fun getLastManga(): Manga? {
        val manga = database.MangaDao().getLastManga()
        return manga
    }

    suspend fun search(search: String): ArrayList<Manga> {
        val coincidences = ArrayList<Manga>()

        with(mangaList.iterator()) {
            forEach {
                if (it.title.contains(search, true)) {
                    coincidences.add(it)
                }
            }
        }

        if (coincidences.isEmpty()) {
            coincidences.addAll(database.MangaDao().search(search))
            mangaList.addAll(coincidences)
        }

        if (coincidences.isEmpty()) {
            try {
                apiService.searchManga(search).forEach { insert(it) }

                with(mangaList.iterator()) {
                    forEach {
                        if (it.title.contains(search, true)) {
                            it.synchronized = true
                            coincidences.add(it)
                        }
                    }
                }
            } catch (e: Exception) {
                printError("cant search the mangas in the server", e)
            }
        }

        return coincidences
    }

    suspend fun searchLocal(search: String): ArrayList<Manga> {
        val coincidences = ArrayList<Manga>()

        if (mangaList.isEmpty())
            mangaList.addAll(database.MangaDao().getAll())

        with(mangaList.iterator()) {
            forEach {
                if (it.title.contains(search, true)) {
                    coincidences.add(it)
                }
            }
        }

        return coincidences
    }

    suspend fun insert(manga: Manga) {
        var m = manga

        // if its not sync
        if (!manga.synchronized) {
            if (manga.author_id != null) {
                try {
                    m = apiService.addManga(manga.title, manga.author_id)[0]
                    m.identifier = manga.identifier
                    m.synchronized = true
                } catch (e: Exception) {
                    printError("cant add the manga to the server", e)
                }
            }
        }

        // add to local repo and db
        val search = searchLocal(m.title)

        if (search.isEmpty()) {
            database.MangaDao().insert(m)
            val res = database.MangaDao().search(m.title)
            mangaList.add(res[0])
        } else {
            update(m)
        }
    }

    suspend fun OLD_insert(manga: Manga) {
        if (manga.author_id != null && !manga.synchronized) {
            try {
                val mangaSynced = apiService.addManga(manga.title, manga.author_id)[0]
                mangaSynced.identifier = manga.identifier
                mangaSynced.synchronized = true

                if (mangaSynced.identifier == 0) {
                    database.MangaDao().insert(mangaSynced)
                } else {
                    with(mangaList.iterator()) {
                        forEach {
                            if (it.identifier == mangaSynced.identifier) {
                                remove()
                                return@forEach
                            }
                        }
                    }
                    mangaList.add(mangaSynced)
                    database.MangaDao().update(mangaSynced)
                }
            } catch (e: Exception) {
                printError("cant add the manga to the server", e)
            }
        } else {
            mangaList.add(manga)
            database.MangaDao().insert(manga)
        }
    }

    suspend fun update(manga: Manga) {
        if (manga.synchronized) {
            Log.i(TAG, "TODO: cant edit mangas when synced with the server!")
        } else {
            if (manga.author_id != null) {
                try {
                    val mangaSynced = apiService.addManga(manga.title, manga.author_id)[0]
                    mangaSynced.identifier = manga.identifier
                    mangaSynced.synchronized = true

                    if (mangaSynced.identifier == 0) {
                        mangaList.add(mangaSynced)
                        database.MangaDao().insert(mangaSynced)
                    } else {
                        with(mangaList.iterator()) {
                            forEach {
                                if (it.identifier == mangaSynced.identifier) {
                                    remove()
                                    return@forEach
                                }
                            }
                        }
                        mangaList.add(mangaSynced)
                        database.MangaDao().update(mangaSynced)
                    }
                } catch (e: Exception) {
                    printError("cant add the manga to the server", e)
                }
            } else {
                with(mangaList.iterator()) {
                    forEach {
                        if (it.identifier == manga.identifier) {
                            remove()
                            return@forEach
                        }
                    }
                }
                mangaList.add(manga)
                database.MangaDao().update(manga)
            }
        }
    }

    suspend fun delete(manga: Manga) {
        mangaList.remove(manga)
        database.MangaDao().delete(manga)
    }

    //#endregion
    //#region private methods

    private fun printError(msg: String, e: Exception) {
        if (M2kApplication.debug) {
            Log.e(TAG, msg)
            e.printStackTrace()
        }
    }

    //#endregion
}