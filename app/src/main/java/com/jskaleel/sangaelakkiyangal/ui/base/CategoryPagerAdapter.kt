package com.jskaleel.sangaelakkiyangal.ui.base

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.jskaleel.sangaelakkiyangal.model.ResponseModel
import com.jskaleel.sangaelakkiyangal.ui.fragments.EttuThogaiFragment
import com.jskaleel.sangaelakkiyangal.ui.fragments.PathinenKizkanakkuFragment
import com.jskaleel.sangaelakkiyangal.ui.fragments.PaththuppattuFragment

class CategoryPagerAdapter(fm: FragmentManager, private val mainLists: List<ResponseModel.MainListResponse>) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return mainLists.size
    }

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return EttuThogaiFragment.newInstance(position)
            1 -> return PaththuppattuFragment.newInstance(position)
            2 -> return PathinenKizkanakkuFragment.newInstance(position)
        }
        return null
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mainLists.get(position).title
    }
}