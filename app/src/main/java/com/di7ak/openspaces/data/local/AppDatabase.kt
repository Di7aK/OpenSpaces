package com.di7ak.openspaces.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.di7ak.openspaces.data.entities.*

@Database(
    entities = [AuthAttributes::class, TopCountEntity::class, LentaItemEntity::class, AuthorEntity::class, Attach::class, CommentItemEntity::class],
    version = 17,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun authDao(): AuthDao

    abstract fun lentaDao(): LentaDao

    abstract fun attachmentsDao(): AttachmentsDao

    abstract fun commentsDao(): CommentsDao

    abstract fun topCountDao(): TopCountDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also {
                    instance = it
                }
            }

        private fun buildDatabase(appContext: Context) =
            Room.databaseBuilder(appContext, AppDatabase::class.java, "spaces")
                .fallbackToDestructiveMigration()
                .build()
    }

}