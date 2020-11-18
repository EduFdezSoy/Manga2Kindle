package es.edufdezsoy.manga2kindle.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import es.edufdezsoy.manga2kindle.data.model.Folder

@Dao
interface FolderDao {
    @Insert
    suspend fun insert(folder: Folder)

    @Update
    suspend fun update(folder: Folder)

    @Delete
    suspend fun delete(folder: Folder)

    @Query("SELECT * FROM Folder")
    fun getAllFolders(): LiveData<List<Folder>>
}