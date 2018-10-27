package com.jskaleel.sangaelakkiyangal.ui.base

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.jskaleel.sangaelakkiyangal.R

class BookTypeHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    internal var txtHeader: TextView = itemView.findViewById<View>(R.id.itemTitle) as TextView
}