package es.edufdezsoy.manga2kindle.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import es.edufdezsoy.manga2kindle.data.model.Language

@Dao
interface LanguageDao {
    @Query("SELECT * FROM language")
    suspend fun getAll(): List<Language>

    @Query("SELECT * FROM language WHERE id = :lang_id")
    suspend fun getLanguage(lang_id: Int): Language

    // TODO: the params in 'LIKE' maye be in between %, search how to edit the var before performing the query
    @Query("SELECT * FROM language WHERE code LIKE :search OR name LIKE :search")
    suspend fun search(search: String): List<Language>

    @Insert
    suspend fun insert(vararg languages: Language)

    @Delete
    suspend fun delete(language: Language)
}