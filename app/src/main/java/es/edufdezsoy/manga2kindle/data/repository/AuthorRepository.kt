package es.edufdezsoy.manga2kindle.data.repository

import android.content.Context
import android.util.Log
import es.edufdezsoy.manga2kindle.M2kApplication
import es.edufdezsoy.manga2kindle.data.M2kDatabase
import es.edufdezsoy.manga2kindle.data.model.Author
import es.edufdezsoy.manga2kindle.network.ApiService

class AuthorRepository {
    private val TAG = M2kApplication.TAG + "_AuthorRepo"
    private val authorList = ArrayList<Author>()
    private val database: M2kDatabase
    private val apiService = ApiService.apiService

    //#region object init

    companion object {
        @Volatile
        private var instance: AuthorRepository? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: AuthorRepository(context).also { instance = it }
        }
    }

    private constructor(context: Context) {
        this.database = M2kDatabase.invoke(context)
    }

    //#endregion
    //#region public methods

    suspend fun getAll(): ArrayList<Author> {
        if (authorList.isEmpty()) {
            authorList.addAll(database.AuthorDao().getAll())

            if (authorList.isEmpty())
                try {
                    apiService.getAllAuthors(null).forEach {
                        insertLocal(it)
                    }
                } catch (e: Exception) {
                    printError("cant get the authors from the server", e)
                }
        }

        return authorList
    }

    suspend fun getAuthor(id: Int): Author? {
        // check the loaded list
        authorList.forEach {
            if (it.id == id)
                return it
        }

        // check the database
        val authorLocal = database.AuthorDao().getAuthor(id)
        if (authorLocal != null) {
            authorList.add(authorLocal)
            return authorLocal
        }

        // check the api
        try {
            val authorServer = apiService.getAuthor(id)[0]
            insertLocal(authorServer)
            return authorServer
        } catch (e: Exception) {
            printError("cant find the author in the server", e)
        }

        // no local or remote, null
        return null
    }

    suspend fun search(search: String): ArrayList<Author> {
        val coincidences = ArrayList<Author>()

        coincidences.addAll(database.AuthorDao().search(search))

        if (coincidences.isEmpty()) {
            try {
                coincidences.addAll(apiService.searchAuthor(search))
            } catch (e: Exception) {
                printError("cant get the authors from the server", e)
            }

            coincidences.forEach {
                insertLocal(it)
            }
        }

        return coincidences
    }

    suspend fun insert(author: Author) = insert(author.name, author.surname, author.nickname)
    suspend fun insert(name: String?, surname: String?, nickname: String?) {
        try {
            val authorServer = apiService.addAuthor(name, surname, nickname)[0]
            insertLocal(authorServer)
        } catch (e: Exception) {
            printError("cant add the author in the server", e)
        }
    }

    suspend fun update(author: Author) {
        database.AuthorDao().update(author)

        authorList.forEach {
            if (it.id == author.id) {
                authorList.remove(it)
                return@forEach
            }
        }
        authorList.add(author)
    }

    suspend fun delete(author: Author) {
        database.AuthorDao().delete(author)
        authorList.remove(author)
    }

    //#endregion
    //#region private methods

    private suspend fun insertLocal(author: Author) {
        //repository
        if (!authorList.contains(author))
            authorList.add(author)

        // database
        database.AuthorDao().insert(author)
    }

    private fun printError(msg: String, e: Exception) {
        if (M2kApplication.debug) {
            Log.e(TAG, msg)
            e.printStackTrace()
        }
    }

    //#endregion
}