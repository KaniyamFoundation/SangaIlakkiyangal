package com.jskaleel.sangaelakkiyangal.ui.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.ViewPager
import com.jskaleel.sangaelakkiyangal.R
import com.jskaleel.sangaelakkiyangal.model.ResponseModel
import com.jskaleel.sangaelakkiyangal.model.SEAppUtil
import com.jskaleel.sangaelakkiyangal.ui.BaseActivity
import com.jskaleel.sangaelakkiyangal.ui.base.CategoryPagerAdapter
import com.jskaleel.sangaelakkiyangal.utils.AppConstants
import com.jskaleel.sangaelakkiyangal.utils.DeviceUtils
import com.jskaleel.sangaelakkiyangal.utils.PrintLog
import com.jskhaleel.hellofreshtest.database.AppDataBase
import com.jskhaleel.hellofreshtest.database.dao.DownloadedBooksDao
import com.jskhaleel.hellofreshtest.database.entities.DownloadedBooks
import com.tonyodev.fetch2.AbstractFetchListener
import com.tonyodev.fetch2.Download
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.annotations.NotNull
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


class HomeActivity : BaseActivity() {
    private lateinit var broadcastManager: LocalBroadcastManager
    private lateinit var booksDao: DownloadedBooksDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        broadcastManager = LocalBroadcastManager.getInstance(applicationContext)
        booksDao = AppDataBase.getAppDatabase(applicationContext).downloadedBooksDao()

        val categoriesList = SEAppUtil.getCategories()
        if (categoriesList == null) {
            callCategory()
        } else {
            viewpager.adapter = CategoryPagerAdapter(supportFragmentManager, categoriesList)
            viewpager.offscreenPageLimit = 2
            setSupportActionBar(toolbar)
            initListener()
        }
        checkPermissionAndDownload()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(1111)
    private fun checkPermissionAndDownload() {
        val perms = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (!EasyPermissions.hasPermissions(this@HomeActivity, *perms)) {
            EasyPermissions.requestPermissions(this, getString(R.string.download_msg_rationale), 1111, *perms)
        }
    }

    private var disposable: Disposable? = null

    private fun callCategory() {
        val categoryCall = SEAppUtil.getRetrofit().getCategories()
        disposable = categoryCall
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    viewpager.adapter = CategoryPagerAdapter(supportFragmentManager, result)
                    viewpager.offscreenPageLimit = 2
                    setSupportActionBar(toolbar)
                    initListener()
                }
    }

    private fun initListener() {
        category_tabs.setupWithViewPager(viewpager)
        category_tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewpager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {

            }
        })
    }

    companion object {
        fun getHomeIntent(context: Context): Intent {
            return Intent(context, HomeActivity::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        fetch.addListener(fetchListener)
    }

    override fun onPause() {
        super.onPause()
        fetch.removeListener(fetchListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        fetch.close()
        disposable?.dispose()
    }

    private val fetchListener = object : AbstractFetchListener() {
        override fun onQueued(@NotNull download: Download, waitingOnNetwork: Boolean) {
            val tag = download.tag!!.split("/")
            val itemPosition = tag[2]

            booksDao.insert(DownloadedBooks(tag[0], tag[1].toInt(), download.file, AppConstants.STATUS_QUEUED))
        }

        override fun onCompleted(@NotNull download: Download) {
            val tag = download.tag!!.split("/")
            val itemPosition = tag[2]
            PrintLog.debug("Khaleel", "Tag : $tag")

            booksDao.updateStatus(AppConstants.STATUS_COMPLETED, tag[1])
            Snackbar.make(mainContent, String.format(getString(R.string.download_complete), tag[0]), Snackbar.LENGTH_LONG).show()

        }

        override fun onError(@NotNull download: Download) {
            val tag = download.tag!!.split("/")
            val itemPosition = tag[2]
            PrintLog.debug("Khaleel", "Tag : $tag")

            booksDao.updateStatus(AppConstants.STATUS_ERROR, tag[1])
            Snackbar.make(mainContent, String.format(getString(R.string.download_error), tag[0]), Snackbar.LENGTH_LONG).show()
        }

        override fun onProgress(@NotNull download: Download, etaInMilliSeconds: Long, downloadedBytesPerSecond: Long) {
            val titleID = download.tag!!.split("/")

            DeviceUtils.sendEventUpdate(applicationContext, broadcastManager, download.id.toLong(),
                    AppConstants.STATUS_DOWNLOADING_CODE, download.progress, Integer.parseInt(titleID[1]),
                    download.downloaded, download.total, 1, titleID[0], true)
        }

        override fun onCancelled(@NotNull download: Download) {
            val titleID = download.tag!!.split("/")
            DeviceUtils.sendEventUpdate(applicationContext, broadcastManager, download.id.toLong(),
                    AppConstants.STATUS_REMOVED_CODE, download.progress, Integer.parseInt(titleID[1]),
                    download.downloaded, download.total, 1, titleID[0], true)
        }
    }
}
