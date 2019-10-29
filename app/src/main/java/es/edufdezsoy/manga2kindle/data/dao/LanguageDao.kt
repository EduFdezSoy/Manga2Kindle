package es.edufdezsoy.manga2kindle.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import es.edufdezsoy.manga2kindle.data.model.Language

@Dao
interface LanguageDao {
    /**
     * Get all the languages in the database
     *
     * @return this list can be empty
     */
    @Query("SELECT * FROM language")
    suspend fun getAll(): List<Language>

    /**
     * Get a language by id
     *
     * @param lang_id the id of the language we are requesting
     * @return the language we requested
     */
    @Query("SELECT * FROM language WHERE id = :lang_id")
    suspend fun getLanguage(lang_id: Int): Language

    /**
     * Search languages that contains a certain string
     *
     * @param search the string we will search for in codes or names
     * @return this list can be empty
     */
    @Query("SELECT * FROM language WHERE instr(code, :search) OR instr(name, :search)")
    suspend fun search(search: String): List<Language>

    /**
     * Insert one or many languages
     */
    @Insert
    suspend fun insert(vararg languages: Language)

    /**
     * Delete a language
     */
    @Delete
    suspend fun delete(language: Language)
}