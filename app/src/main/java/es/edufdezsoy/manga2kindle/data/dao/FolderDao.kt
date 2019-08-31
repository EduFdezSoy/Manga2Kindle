package es.edufdezsoy.manga2kindle.data.dao

import androidx.room.*
import es.edufdezsoy.manga2kindle.data.model.Folder

@Dao
interface FolderDao {
    @Query("SELECT * FROM folder")
    suspend fun getAll(): List<Folder>

    @Query("SELECT id FROM folder ORDER BY id DESC LIMIT 1")
    suspend fun getLastIndex(): Int?

    @Insert
    suspend fun insert(vararg folders: Folder)

    @Update
    suspend fun update(folder: Folder)

    @Delete
    suspend fun delete(folder: Folder)
}