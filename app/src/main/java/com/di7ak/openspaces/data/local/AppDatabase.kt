package com.di7ak.openspaces.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.di7ak.openspaces.data.entities.AuthAttributes
import com.di7ak.openspaces.ui.features.lenta.Author
import com.di7ak.openspaces.ui.features.lenta.LentaModel

@Database(entities = [AuthAttributes::class, LentaModel::class, Author::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun authDao(): AuthDao

    abstract fun lentaDao(): LentaDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) { instance ?: buildDatabase(context).also { instance = it } }

        private fun buildDatabase(appContext: Context) =
            Room.databaseBuilder(appContext, AppDatabase::class.java, "spaces")
                .fallbackToDestructiveMigration()
                .build()
    }

}