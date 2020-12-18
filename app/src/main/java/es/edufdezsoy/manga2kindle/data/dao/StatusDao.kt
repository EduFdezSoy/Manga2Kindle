package es.edufdezsoy.manga2kindle.data.dao

import androidx.room.*
import es.edufdezsoy.manga2kindle.data.model.Status
import kotlinx.coroutines.flow.Flow

@Dao
interface StatusDao {
    @Insert
    suspend fun insert(status: Status)

    @Update
    suspend fun update(status: Status)

    @Delete
    suspend fun delete(status: Status)

    @Query("SELECT * FROM Status ORDER BY id DESC")
    fun getAllStatuses(): Flow<List<Status>>

    @Query("SELECT * FROM status WHERE id = :id")
    suspend fun getById(id: Int): Status?
}