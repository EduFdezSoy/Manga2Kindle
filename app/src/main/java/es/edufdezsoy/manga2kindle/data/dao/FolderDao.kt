package es.edufdezsoy.manga2kindle.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import es.edufdezsoy.manga2kindle.data.model.Folder

@Dao
interface FolderDao {
    @Insert
    fun insert(folder: Folder)

    @Update
    fun update(folder: Folder)

    @Delete
    fun delete(folder: Folder)

    @Query("SELECT * FROM Folder")
    fun getAllFolders(): LiveData<List<Folder>>

    @Query("SELECT * FROM Folder")
    suspend fun getStaticAllFolders(): List<Folder>
    @Query("SELECT * FROM Folder WHERE active = 1")
    fun getStaticActiveFolders(): LiveData<List<Folder>>
}

// TODO-WAS DOING: suspend no longer valid here, move the crap to LiveData.