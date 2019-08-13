package es.edufdezsoy.manga2kindle.data

import androidx.room.Database
import androidx.room.RoomDatabase
import es.edufdezsoy.manga2kindle.data.dao.AuthorDao
import es.edufdezsoy.manga2kindle.data.model.Author

@Database(entities = arrayOf(Author::class), version = 1)
abstract class M2kDatabase : RoomDatabase() {
    abstract fun authorDao(): AuthorDao
}
