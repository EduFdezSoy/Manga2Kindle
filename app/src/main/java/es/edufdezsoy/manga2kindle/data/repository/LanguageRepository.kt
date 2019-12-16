package es.edufdezsoy.manga2kindle.data.repository

import android.content.Context
import android.util.Log
import es.edufdezsoy.manga2kindle.M2kApplication
import es.edufdezsoy.manga2kindle.data.M2kDatabase
import es.edufdezsoy.manga2kindle.data.model.Language
import es.edufdezsoy.manga2kindle.network.ApiService

class LanguageRepository {
    private val TAG = M2kApplication.TAG + "_LangRepo"
    private val langList = ArrayList<Language>()
    private val database: M2kDatabase
    private val apiService = ApiService.apiService

    //#region object init

    companion object {
        @Volatile
        private var instance: LanguageRepository? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: LanguageRepository(context).also { instance = it }
        }
    }

    private constructor(context: Context) {
        this.database = M2kDatabase.invoke(context)
    }

    //#endregion
    //#region public methods

    suspend fun getAll(): ArrayList<Language> {
        if (langList.isEmpty()) {
            langList.addAll(database.LanguageDao().getAll())
            if (langList.isEmpty()) {
                try {
                    langList.addAll(apiService.getAllLanguages())
                } catch (e: Exception) {
                    printError("cant get all languages", e)
                }
            }
        }
        return langList
    }

    suspend fun getLanguage(lang_id: Int): Language? {
        // try local
        if (langList.isNotEmpty()) {
            langList.forEach {
                if (it.id == lang_id)
                    return it
            }
        } else {
            return database.LanguageDao().getLanguage(lang_id)
        }

        // call api
        try {
            TODO("get a language by id from server")
        } catch (e: Exception) {
            printError("there is no method implemented to retrieve a lang by id", e)
        }

        // nothing? ok, null
        return null
    }

    suspend fun search(search: String): ArrayList<Language> {
        val coincidences = ArrayList<Language>()

        if (langList.isNotEmpty()) {
            langList.forEach {
                if (it.name.contains(search, true) || it.code.contains(search, true))
                    coincidences.add(it)
            }
        } else {
            coincidences.addAll(database.LanguageDao().search(search))
        }

        if (coincidences.isEmpty())
            try {
                TODO("search a language in the server")
            } catch (e: Exception) {
                printError("there is no method implemented to search a lang", e)
            }

        return coincidences
    }

    suspend fun insert(language: Language) {
        langList.add(language)
        database.LanguageDao().insert(language)
    }

    suspend fun delete(language: Language) {
        langList.remove(language)
        database.LanguageDao().delete(language)
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