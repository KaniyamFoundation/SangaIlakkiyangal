package com.jskaleel.sangaelakkiyangal.listeners

import com.jskaleel.sangaelakkiyangal.model.ResponseModel

interface DownloadItemListener {
    fun downloadSelectedBook(singleItem: ResponseModel.BooksItem)
}