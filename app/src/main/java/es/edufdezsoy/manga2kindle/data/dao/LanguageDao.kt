package es.edufdezsoy.manga2kindle.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import es.edufdezsoy.manga2kindle.data.model.Language

@Dao
interface LanguageDao {
    @Query("SELECT * FROM language")
    fun getAll(): List<Language>

    @Query("SELECT * FROM language WHERE id = :lang_id")
    fun getLanguage(lang_id: Int): Language

    // TODO: the params in 'LIKE' maye be in between %, search how to edit the var before performing the query
    @Query("SELECT * FROM language WHERE code LIKE :search OR name LIKE :search")
    fun search(search: String): List<Language>

    @Insert
    fun insert(vararg languages: Language)

    @Delete
    fun delete(language: Language)
}