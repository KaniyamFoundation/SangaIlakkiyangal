package com.jskaleel.sangaelakkiyangal.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import com.jskaleel.sangaelakkiyangal.R
import com.jskaleel.sangaelakkiyangal.model.SEAppUtil
import com.jskaleel.sangaelakkiyangal.ui.BaseActivity
import com.jskaleel.sangaelakkiyangal.ui.base.CategoryPagerAdapter
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val categoriesList = SEAppUtil.getCategories()

        viewpager.adapter = CategoryPagerAdapter(supportFragmentManager, categoriesList)
        viewpager.offscreenPageLimit = 2

        setSupportActionBar(toolbar)

        initListener()
    }

    private fun initListener() {
        category_tabs.setupWithViewPager(viewpager)
        category_tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewpager.currentItem = tab.position
                updateTabView(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {

            }
        })
    }

    private fun updateTabView(position: Int) {
        when (position) {
            0 -> {
                appbar.background = getDrawable(R.color.primary_blue)
                window.statusBarColor = ContextCompat.getColor(this, R.color.primary_dark_blue)
            }
            1 -> {
                appbar.background = getDrawable(R.color.primary_red)
                window.statusBarColor = ContextCompat.getColor(this, R.color.primary_dark_red)
            }
            2 -> {
                appbar.background = getDrawable(R.color.primary_green)
                window.statusBarColor = ContextCompat.getColor(this, R.color.primary_dark_green)
            }
        }
    }

    companion object {
        fun getHomeIntent(context: Context): Intent {
            return Intent(context, HomeActivity::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
