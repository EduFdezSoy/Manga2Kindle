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
    version = 2
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
            .addMigrations()
            .fallbackToDestructiveMigrationFrom(1)
            .fallbackToDestructiveMigrationOnDowngrade()
            .build()
    })
}