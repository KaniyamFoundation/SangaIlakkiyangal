package com.jskaleel.sangaelakkiyangal.booksdb

import ninja.sakib.pultusorm.annotations.PrimaryKey

class BookDB {
    @PrimaryKey
    var title: String? = null
    var bookId: Int = 0
    var bookUrl: String? = null
    var downloadStatus: String? = null
}
