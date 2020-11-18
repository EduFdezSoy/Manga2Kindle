package es.edufdezsoy.manga2kindle.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import es.edufdezsoy.manga2kindle.data.dao.ChapterDao
import es.edufdezsoy.manga2kindle.data.dao.FolderDao
import es.edufdezsoy.manga2kindle.data.dao.MangaDao
import es.edufdezsoy.manga2kindle.data.model.*
import es.edufdezsoy.manga2kindle.utils.SingletonHolder

@Database(
    entities = [
        Manga::class,
        Author::class,
        Chapter::class,
        Folder::class,
        MangaAuthorCrossRef::class
    ],
    version = 1
)
abstract class M2KDatabase : RoomDatabase() {

    abstract fun folderDao(): FolderDao
    abstract fun mangaDao(): MangaDao
    abstract fun chapterDao(): ChapterDao

    companion object : SingletonHolder<M2KDatabase, Context>({
        Room.databaseBuilder(
            it.applicationContext,
            M2KDatabase::class.java,
            "manga2kindle_database"
        )
            .fallbackToDestructiveMigration()
            .fallbackToDestructiveMigrationOnDowngrade()
//            .addCallback(
//                object : RoomDatabase.Callback() {
//                    override fun onCreate(db: SupportSQLiteDatabase) {
//                        super.onCreate(db)
//                        val noteDao = ChapterDatabase.getInstance(it.applicationContext).chapterDao()
//                        GlobalScope.launch {
//                            noteDao.insert(
//                                Chapter(
//                                    "Kaguya-sama: Love is War",
//                                    "Aka Akasaka",
//                                    "The wholesomest chapter",
//                                    92,
//                                    8,
//                                    ""
//                                )
//                            )
//                            noteDao.insert(Chapter("Legit Manga", "", "Dumb Title", 5, null, ""))
//                            noteDao.insert(
//                                Chapter(
//                                    "One-punch Man",
//                                    "ONE, Murata",
//                                    "Punch it",
//                                    120,
//                                    12,
//                                    ""
//                                )
//                            )
//                            noteDao.insert(
//                                Chapter(
//                                    "Kaguya-sama: Love is War",
//                                    "Aka Akasaka",
//                                    "The wholesomest chapter",
//                                    123,
//                                    14,
//                                    ""
//                                )
//                            )
//                            noteDao.insert(Chapter("Legit Manga", "", "Dumb Title", 456, null, ""))
//                            noteDao.insert(
//                                Chapter(
//                                    "One-punch Man",
//                                    "ONE, Murata",
//                                    "Punch it",
//                                    789,
//                                    94,
//                                    ""
//                                )
//                            )
//                            noteDao.insert(Chapter("Legit Manga", "", "Who knows", 0, 0, ""))
//                        }
//                    }
//                }
//            )
            .build()
    })
}