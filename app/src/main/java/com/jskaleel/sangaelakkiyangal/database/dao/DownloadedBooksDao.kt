package com.jskhaleel.hellofreshtest.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.jskhaleel.hellofreshtest.database.entities.DownloadedBooks

@Dao
interface DownloadedBooksDao {
    @Query("SELECT * from downloadedBooks")
    fun getAllDownloads(): List<DownloadedBooks>

    @Insert(onConflict = REPLACE)
    fun insert(downloadedBooks: DownloadedBooks)

    @Query("UPDATE downloadedBooks SET status = :status WHERE book_id = :bookId")
    fun updateStatus(status: String, bookId: String)

    @Query("DELETE from downloadedBooks WHERE book_id = :bookId")
    fun deleteRecipe(bookId: String)

    @Query("DELETE from downloadedBooks")
    fun deleteAll()

    @Query("SELECT * from downloadedBooks WHERE book_id = :bookId")
    fun isIdAvailable(bookId: String): Boolean
}