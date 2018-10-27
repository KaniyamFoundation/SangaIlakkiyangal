package com.jskhaleel.hellofreshtest.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "downloadedBooks")
data class DownloadedBooks(
        @ColumnInfo(name = "title")
        var title: String,

        @PrimaryKey(autoGenerate = false)
        @ColumnInfo(name = "book_id")
        var bookId: Int,

        @ColumnInfo(name = "book_url")
        var bookUrl: String,

        @ColumnInfo(name = "status")
        var downloadStatus: String)
