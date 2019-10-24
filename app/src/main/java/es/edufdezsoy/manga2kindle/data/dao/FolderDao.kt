package es.edufdezsoy.manga2kindle.data.dao

import androidx.room.*
import es.edufdezsoy.manga2kindle.data.model.Folder

@Dao
interface FolderDao {
    /**
     * Get all the folders in the database
     *
     * @return this list can be empty
     */
    @Query("SELECT * FROM folder")
    suspend fun getAll(): List<Folder>

    /**
     * Get the last folder id inserted in the database
     *
     * @return the last id or null if the database is empty
     */
    @Query("SELECT id FROM folder ORDER BY id DESC LIMIT 1")
    suspend fun getLastIndex(): Int?

    /**
     * Insert one or many folders
     */
    @Insert
    suspend fun insert(vararg folders: Folder)

    /**
     * Update a folder
     */
    @Update
    suspend fun update(folder: Folder)

    /**
     * Delete a folder
     */
    @Delete
    suspend fun delete(folder: Folder)
}