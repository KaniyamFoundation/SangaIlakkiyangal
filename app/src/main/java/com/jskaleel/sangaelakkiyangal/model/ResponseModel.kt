package com.jskaleel.sangaelakkiyangal.model

import com.jskaleel.sangaelakkiyangal.utils.AppConstants

object ResponseModel {
    data class MainListResponse(val title: String)
    data class CategoryItemResponse(val title: String, val books: ArrayList<BooksItem>)

    data class BooksItem(val book_id: String, val title: String, val epub_url: String,
                         val cover_url: String, val format: String, var status: String = AppConstants.STATUS_NONE)
}