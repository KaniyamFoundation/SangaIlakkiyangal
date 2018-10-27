package com.jskaleel.sangaelakkiyangal.ui.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem

import com.jskaleel.sangaelakkiyangal.R
import com.jskaleel.sangaelakkiyangal.model.ResponseModel
import com.jskaleel.sangaelakkiyangal.ui.BaseActivity
import com.jskaleel.sangaelakkiyangal.ui.base.CategoryDetailsListAdapter
import com.jskaleel.sangaelakkiyangal.utils.AppConstants
import kotlinx.android.synthetic.main.activity_category_details.*
import android.support.v7.widget.DividerItemDecoration
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import com.jskaleel.sangaelakkiyangal.listeners.BookItemClickListener
import com.jskaleel.sangaelakkiyangal.utils.DeviceUtils
import com.jskaleel.sangaelakkiyangal.utils.PrintLog
import com.jskhaleel.hellofreshtest.database.AppDataBase
import com.jskhaleel.hellofreshtest.database.dao.DownloadedBooksDao
import com.jskhaleel.hellofreshtest.database.entities.DownloadedBooks
import com.tonyodev.fetch2.*
import org.jetbrains.annotations.NotNull


class CategoryDetailsActivity : BaseActivity(), BookItemClickListener {
    override fun onItemClickListener(singleItem: ResponseModel.BooksItem, itemPosition: Int, type: Int) {
        if (type == 1002) {
            openItem(singleItem)
        } else {
            downloadItem(singleItem, itemPosition)
        }
    }

    lateinit var selectedCategory: ResponseModel.CategoryItemResponse
    private lateinit var broadcastManager: LocalBroadcastManager

    companion object {
        fun getDetailsIntent(context: Context, selectedItem: ResponseModel.CategoryItemResponse): Intent {
            val intent = Intent(context, CategoryDetailsActivity::class.java)
            intent.putExtra(AppConstants.KEY_CATEGORY_ITEM_RESPONSE, selectedItem)
            return intent
        }
    }

    private fun openItem(singleItem: ResponseModel.BooksItem) {
        val uri = Uri.parse(DeviceUtils.getAppDirectory(applicationContext).absolutePath + "/${singleItem.title}.${singleItem.format}")
        startActivity(PdfViewerActivity.getPdfViewerIntent(applicationContext, uri))
    }

    private fun downloadItem(singleItem: ResponseModel.BooksItem, itemPosition: Int) {
        val url = singleItem.epub_url
        val file = DeviceUtils.getAppDirectory(applicationContext)
        if (!file.exists()) {
            file.mkdirs()
        }
        val filePath = file.toString() + "/${singleItem.title}.${singleItem.format}"
        val request = Request(url, filePath)
        request.priority = Priority.HIGH
        request.enqueueAction = EnqueueAction.REPLACE_EXISTING
        request.tag = "${singleItem.title}/${singleItem.book_id}/$itemPosition"

        fetch.enqueue(request)
    }

    private lateinit var booksDao: DownloadedBooksDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_details)

        booksDao = AppDataBase.getAppDatabase(applicationContext).downloadedBooksDao()

        broadcastManager = LocalBroadcastManager.getInstance(applicationContext)
        if (intent != null && intent.hasExtra(AppConstants.KEY_CATEGORY_ITEM_RESPONSE)) {
            selectedCategory = intent.getSerializableExtra(AppConstants.KEY_CATEGORY_ITEM_RESPONSE) as ResponseModel.CategoryItemResponse
            toolbar.title = selectedCategory.title
        }

        setSupportActionBar(toolbar)

        val ab = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)

        val downloadedBooks = booksDao.getAllDownloads()
        for (downloadItem in downloadedBooks) {
            for (bookItem in selectedCategory.books) {
                if (downloadItem.bookId == bookItem.book_id.toInt()) {
                    bookItem.status = downloadItem.downloadStatus
                }
            }
        }

        initUI()
    }

    private lateinit var listAdapter: CategoryDetailsListAdapter

    private fun initUI() {
        rvBookList.setHasFixedSize(true)
        val llManager = LinearLayoutManager(applicationContext)
        llManager.orientation = LinearLayoutManager.VERTICAL

        rvBookList.layoutManager = llManager
        val itemDecorator = DividerItemDecoration(applicationContext, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.item_decor)!!)
        rvBookList.addItemDecoration(itemDecorator)

        listAdapter = CategoryDetailsListAdapter(applicationContext, selectedCategory.books, this@CategoryDetailsActivity)
        rvBookList.adapter = listAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
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
    }

    private val fetchListener = object : AbstractFetchListener() {
        override fun onQueued(@NotNull download: Download, waitingOnNetwork: Boolean) {
            val tag = download.tag!!.split("/")
            val itemPosition = tag[2]

            listAdapter.updateItemStatus(AppConstants.STATUS_QUEUED, itemPosition.toInt())
            booksDao.insert(DownloadedBooks(tag[0], tag[1].toInt(), download.file, AppConstants.STATUS_QUEUED))
        }

        override fun onCompleted(@NotNull download: Download) {
            val tag = download.tag!!.split("/")
            val itemPosition = tag[2]
            PrintLog.debug("Khaleel", "Tag : $tag")

            listAdapter.updateItemStatus(AppConstants.STATUS_COMPLETED, itemPosition.toInt())
            booksDao.updateStatus(AppConstants.STATUS_COMPLETED, tag[1])
            Snackbar.make(rvBookList, String.format(getString(R.string.download_complete), tag[0]), Snackbar.LENGTH_LONG).show()

            DeviceUtils.sendEventUpdate(applicationContext, broadcastManager, download.id.toLong(),
                    AppConstants.STATUS_DONE_CODE, download.progress, Integer.parseInt(tag[1]),
                    download.downloaded, download.total, 1, tag[0], true)
        }

        override fun onError(@NotNull download: Download) {
            val tag = download.tag!!.split("/")
            val itemPosition = tag[2]
            PrintLog.debug("Khaleel", "Tag : $tag")

            listAdapter.updateItemStatus(AppConstants.STATUS_ERROR, itemPosition.toInt())
            booksDao.updateStatus(AppConstants.STATUS_ERROR, tag[1])
            Snackbar.make(rvBookList, String.format(getString(R.string.download_error), tag[0]), Snackbar.LENGTH_LONG).show()
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
