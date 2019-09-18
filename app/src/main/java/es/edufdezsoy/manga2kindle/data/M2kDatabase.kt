package es.edufdezsoy.manga2kindle.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import es.edufdezsoy.manga2kindle.data.dao.*
import es.edufdezsoy.manga2kindle.data.model.*

@Database(
    entities = [Folder::class, Author::class, Manga::class, Language::class, Chapter::class],
    version = 3
)
abstract class M2kDatabase : RoomDatabase() {
    abstract fun FolderDao(): FolderDao
    abstract fun AuthorDao(): AuthorDao
    abstract fun MangaDao(): MangaDao
    abstract fun LanguageDao(): LanguageDao
    abstract fun ChapterDao(): ChapterDao

    companion object {
        @Volatile
        private var instance: M2kDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, M2kDatabase::class.java, "manga2kindle.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}
