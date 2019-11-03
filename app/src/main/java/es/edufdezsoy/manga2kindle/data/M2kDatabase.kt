package es.edufdezsoy.manga2kindle.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import es.edufdezsoy.manga2kindle.data.dao.*
import es.edufdezsoy.manga2kindle.data.model.*
import java.util.*

@Database(
    entities = [Folder::class, Author::class, Manga::class, Language::class, Chapter::class],
    version = 7
)
@TypeConverters(Converters::class)
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
                .addMigrations(MIGRATION_4_5)
                .addMigrations(MIGRATION_5_6)
                .addMigrations(MIGRATION_6_7)
//                .fallbackToDestructiveMigration()
                .build()

        // Previous migrations aren't available since the app was in a really early stage, so no need to

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // add the new column
                database.execSQL("ALTER TABLE chapter ADD status NOT NULL DEFAULT(0)")
                database.execSQL("UPDATE chapter SET status = (SELECT CASE WHEN sended = 1 THEN 3 ELSE 0 END FROM chapter AS b WHERE chapter.identifier = b.identifier)")

                // rename the table
                database.execSQL("ALTER TABLE chapter RENAME TO chapter_old")
                // recreate the table without the dropped column
                database.execSQL("CREATE TABLE IF NOT EXISTS `chapter` (`identifier` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `status` INTEGER NOT NULL, `id` INTEGER, `manga_id` INTEGER NOT NULL, `lang_id` INTEGER, `volume` INTEGER, `chapter` REAL NOT NULL, `title` TEXT, `file_path` TEXT, `checksum` TEXT, `delivered` INTEGER NOT NULL, `error` INTEGER NOT NULL, `reason` TEXT, `visible` INTEGER NOT NULL)")

                // migrate data from old to new table
                database.execSQL("INSERT INTO chapter SELECT identifier, status, id, manga_id, lang_id, volume, chapter, title, file_path, checksum, delivered, error, reason, visible FROM chapter_old")
                database.execSQL("DROP TABLE chapter_old")
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE chapter ADD upload_date INTEGER")

                // lets add content to those chapters that where already uploaded
                val a = Converters().dateToTimestamp(Calendar.getInstance().getTime())
                database.execSQL("UPDATE chapter SET upload_date = $a WHERE status != 0")
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE chapter ADD enqueue_date INTEGER")

                // update the content to those chapters that where already uploaded
                database.execSQL("UPDATE chapter SET enqueue_date = upload_date WHERE upload_date != null")
            }
        }
    }
}
