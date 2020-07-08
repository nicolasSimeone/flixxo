package com.flixxo.apps.flixxoapp.repositories.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.flixxo.apps.flixxoapp.model.Category
import com.flixxo.apps.flixxoapp.model.ContentPurchased
import com.flixxo.apps.flixxoapp.model.Language

@Database(entities = [Category::class, Language::class, ContentPurchased::class], version = 7)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun languageDao(): LanguageDao
    abstract fun purchaseDao(): PurchasedDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private val ROOM_DB_NAME = "flixxo.db"

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    ROOM_DB_NAME
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
