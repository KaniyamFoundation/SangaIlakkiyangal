package com.jskhaleel.hellofreshtest.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.jskhaleel.hellofreshtest.database.dao.DownloadedBooksDao
import com.jskhaleel.hellofreshtest.database.entities.DownloadedBooks


@Database(entities = [DownloadedBooks::class], version = 1, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {

    abstract fun downloadedBooksDao(): DownloadedBooksDao

    companion object {
        private var instance: AppDataBase? = null

        fun getAppDatabase(context: Context): AppDataBase {
            if (instance == null) {
                instance = Room.databaseBuilder(context.applicationContext,
                        AppDataBase::class.java,
                        "favourite_recipe.db")
                        .allowMainThreadQueries()
                        .build()
            }
            return instance as AppDataBase
        }
    }
}