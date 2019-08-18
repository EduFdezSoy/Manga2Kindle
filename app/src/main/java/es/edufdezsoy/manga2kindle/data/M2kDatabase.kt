package es.edufdezsoy.manga2kindle.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import es.edufdezsoy.manga2kindle.data.dao.AuthorDao
import es.edufdezsoy.manga2kindle.data.dao.ChapterDao
import es.edufdezsoy.manga2kindle.data.dao.LanguageDao
import es.edufdezsoy.manga2kindle.data.dao.MangaDao
import es.edufdezsoy.manga2kindle.data.model.Author
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.data.model.Language
import es.edufdezsoy.manga2kindle.data.model.Manga

@Database(
    entities = [Author::class, Manga::class, Language::class, Chapter::class],
    version = 2
)
abstract class M2kDatabase : RoomDatabase() {
    abstract fun authorDao(): AuthorDao
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
            Room.databaseBuilder(context, M2kDatabase::class.java, "manga2kindle.db").build()
    }
}
