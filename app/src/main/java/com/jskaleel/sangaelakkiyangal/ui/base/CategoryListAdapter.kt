package com.jskaleel.sangaelakkiyangal.ui.base

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jskaleel.sangaelakkiyangal.R
import com.jskaleel.sangaelakkiyangal.listeners.CategoryItemClickListener
import com.jskaleel.sangaelakkiyangal.model.ResponseModel

class CategoryListAdapter(val context: Context, var bookList: List<ResponseModel.CategoryItemResponse>,
                          val categoryItemClickListener: CategoryItemClickListener) : RecyclerView.Adapter<BookTypeHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookTypeHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.book_list_item, parent, false)
        val lp = view.layoutParams as StaggeredGridLayoutManager.LayoutParams
        lp.height = parent.measuredHeight / 6
        view.layoutParams = lp;
        return BookTypeHolder(view)
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun onBindViewHolder(holder: BookTypeHolder, position: Int) {
        val singleItem = bookList[position]
        holder.txtHeader.text = singleItem.title

        holder.itemView.setOnClickListener {
            categoryItemClickListener.onItemClickListener(singleItem, holder.adapterPosition)
        }
    }

    fun setItems(result: List<ResponseModel.CategoryItemResponse>) {
        bookList = result
        notifyDataSetChanged()
    }
}