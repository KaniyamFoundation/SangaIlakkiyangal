package com.jskaleel.sangaelakkiyangal.ui.base

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.jskaleel.sangaelakkiyangal.R
import com.jskaleel.sangaelakkiyangal.model.ResponseModel
import com.jskaleel.sangaelakkiyangal.utils.AppConstants
import android.content.Intent
import android.net.Uri
import com.jskaleel.sangaelakkiyangal.listeners.BookItemClickListener
import com.jskhaleel.hellofreshtest.database.AppDataBase
import com.jskhaleel.hellofreshtest.database.dao.DownloadedBooksDao


class CategoryDetailsListAdapter(private val context: Context,
                                 private val books: ArrayList<ResponseModel.BooksItem>,
                                 private val bookItemClickListener: BookItemClickListener) : RecyclerView.Adapter<CategoryDetailsListAdapter.BookViewHolder>() {

    private var booksDao: DownloadedBooksDao = AppDataBase.getAppDatabase(context).downloadedBooksDao()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryDetailsListAdapter.BookViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.book_item_view, parent, false)
        return BookViewHolder(view)
    }

    override fun getItemCount(): Int {
        return books.size
    }

    override fun onBindViewHolder(holder: CategoryDetailsListAdapter.BookViewHolder, position: Int) {
        val singleItem = books[position]
        holder.bookTitle.text = singleItem.title
        Glide.with(context)
                .load(singleItem.cover_url)
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                .apply(RequestOptions().centerCrop())
                .into(holder.ivBookCover)

        holder.itemView.setOnClickListener {
            showPopupMenu(holder.itemView, singleItem, holder.adapterPosition)
        }

        if (singleItem.status == AppConstants.STATUS_QUEUED) {
            holder.downloadStatus.text = "Downloading..."
            holder.itemView.tag = AppConstants.STATUS_QUEUED
        } else if (singleItem.status == AppConstants.STATUS_COMPLETED) {
            holder.downloadStatus.text = "Completed..."
            holder.itemView.tag = AppConstants.STATUS_COMPLETED
        } else if (singleItem.status == AppConstants.STATUS_ERROR) {
            holder.downloadStatus.text = "Failed..."
            holder.itemView.tag = AppConstants.STATUS_ERROR
        } else {
            holder.downloadStatus.text = ""
            holder.itemView.tag = AppConstants.STATUS_NONE
        }
    }

    private fun showPopupMenu(it: View, singleItem: ResponseModel.BooksItem, itemPosition: Int) {
        val popupMenu = PopupMenu(context, it)
        if (it.tag == AppConstants.STATUS_COMPLETED) {
            popupMenu.menu.add(Menu.NONE, 1002, 1,
                    R.string.open)
        } else {
            popupMenu.menu.add(Menu.NONE, 1001, 1,
                    R.string.download)
        }

        popupMenu.menu.add(Menu.NONE, 1003, 2,
                R.string.open_in_browser)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                1001 -> {
                    bookItemClickListener.onItemClickListener(singleItem, itemPosition, 1001)
                    true
                }
                1002 -> {
                    bookItemClickListener.onItemClickListener(singleItem, itemPosition, 1002)
                    true
                }
                1003 -> {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(singleItem.epub_url))
                    context?.startActivity(browserIntent)
                    true
                }
                else -> false
            }

        }
        popupMenu.show()
    }

    class BookViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var ivBookCover: ImageView = itemView.findViewById<View>(R.id.ivBookCover) as ImageView
        internal var bookTitle: TextView = itemView.findViewById<View>(R.id.bookTitle) as TextView
        internal var downloadStatus: TextView = itemView.findViewById<View>(R.id.downloadStatus) as TextView
    }

    fun updateItemStatus(status: String, itemPosition: Int) {
        books[itemPosition].status = status
        notifyItemChanged(itemPosition)
    }
}