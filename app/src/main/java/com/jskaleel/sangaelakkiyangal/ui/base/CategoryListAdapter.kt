package com.jskaleel.sangaelakkiyangal.ui.base

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jskaleel.sangaelakkiyangal.R
import com.jskaleel.sangaelakkiyangal.listeners.BookItemClickListener
import com.jskaleel.sangaelakkiyangal.model.ResponseModel
import com.jskaleel.sangaelakkiyangal.utils.PrintLog

class CategoryListAdapter(val context: Context?, var bookList: List<ResponseModel.CategoryItemResponse>,
                          val bookItemClickListener: BookItemClickListener) : RecyclerView.Adapter<BookTypeHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookTypeHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.book_list_item, parent, false)
        return BookTypeHolder(view)
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    private lateinit var horizonListAdapter: HorizontalCategoryListAdapter
    private val horizontalAdapterList : MutableMap<String, HorizontalCategoryListAdapter> = mutableMapOf()
    override fun onBindViewHolder(holder: BookTypeHolder, position: Int) {
        val singleItem = bookList[position]
        holder.txtHeader.text = singleItem.title

        horizonListAdapter = HorizontalCategoryListAdapter(context, singleItem.books, bookItemClickListener, holder.adapterPosition)
        holder.rvHorizontalList.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        holder.rvHorizontalList.layoutManager = layoutManager

        holder.rvHorizontalList.adapter = horizonListAdapter
        horizontalAdapterList[singleItem.title] = horizonListAdapter
    }

    fun setItems(result: List<ResponseModel.CategoryItemResponse>) {
        bookList = result
        notifyDataSetChanged()
    }

    fun updateItemStatus(bookItem: ResponseModel.BooksItem, horizontalPosition: Int, itemPosition: Int) {
        bookList[horizontalPosition].books[itemPosition] = bookItem
        (horizontalAdapterList[bookList[horizontalPosition].title])!!.notifyItemChanged(itemPosition)
    }
}