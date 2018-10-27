package com.jskaleel.sangaelakkiyangal.listeners

import com.jskaleel.sangaelakkiyangal.model.ResponseModel

interface CategoryItemClickListener {

    fun onItemClickListener(singleItem: ResponseModel.CategoryItemResponse, itemPosition: Int)
}