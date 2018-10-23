package com.jskaleel.sangaelakkiyangal.listeners

import com.jskaleel.sangaelakkiyangal.model.ResponseModel

interface BookItemClickListener {

    fun onItemClickListener(singleItem: ResponseModel.BooksItem, horizontalPosition: Int, itemPosition: Int, itemTag : Int)
}