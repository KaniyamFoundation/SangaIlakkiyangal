package com.jskaleel.sangaelakkiyangal.ui.fragments

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jskaleel.sangaelakkiyangal.R
import com.jskaleel.sangaelakkiyangal.booksdb.BookDB
import com.jskaleel.sangaelakkiyangal.listeners.ActionListener
import com.jskaleel.sangaelakkiyangal.listeners.BookItemClickListener
import com.jskaleel.sangaelakkiyangal.model.ResponseModel
import com.jskaleel.sangaelakkiyangal.model.SEAppUtil
import com.jskaleel.sangaelakkiyangal.ui.base.CategoryListAdapter
import com.jskaleel.sangaelakkiyangal.utils.AppConstants
import com.jskaleel.sangaelakkiyangal.utils.DeviceUtils
import com.jskaleel.sangaelakkiyangal.utils.PrintLog
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.Downloader
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ninja.sakib.pultusorm.core.PultusORM
import ninja.sakib.pultusorm.core.PultusORMCondition
import ninja.sakib.pultusorm.core.PultusORMUpdater
import org.jetbrains.annotations.NotNull
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import android.util.Log
import com.jskaleel.sangaelakkiyangal.ui.activities.PdfViewerActivity


class EttuThogaiFragment : Fragment(), BookItemClickListener, ActionListener {

    val RC_READ_WRITE_STORAGE = 1111
    private lateinit var fetch: Fetch
    private lateinit var mContext: Context

    private lateinit var broadcastManager: LocalBroadcastManager

    private lateinit var pultusORM: PultusORM

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context!!

        broadcastManager = LocalBroadcastManager.getInstance(context)

        val fetchConfiguration = FetchConfiguration.Builder(mContext)
                .enableRetryOnNetworkGain(true)
                .setDownloadConcurrentLimit(1)
                .setGlobalNetworkType(NetworkType.ALL)
                .setNamespace("SANGA_ELAKKIYAM")
                .setProgressReportingInterval(2000L)
                .setHttpDownloader(HttpUrlConnectionDownloader(Downloader.FileDownloaderType.SEQUENTIAL))
                .build()
        Fetch.setDefaultInstanceConfiguration(fetchConfiguration)

        fetch = Fetch.getInstance(fetchConfiguration)

        val appPath: String = mContext.filesDir.absolutePath
        pultusORM = PultusORM("sanga_elakkiyangal.db", appPath)
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
        if (EasyPermissions.hasPermissions(this.activity!!, *perms)) {
            if (itemTag == 1002) {
                openItem()
            } else {
                downloadItem()
            }
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.download_msg_rationale),
                    RC_READ_WRITE_STORAGE, *perms)
        }
    }

    private fun openItem() {
        val uri = Uri.parse(DeviceUtils.getAppDirectory(mContext).absolutePath + "/${selectedBookItem.title}.${selectedBookItem.format}")
        startActivity(PdfViewerActivity.getPdfViewerIntent(mContext, uri))
    }

    private fun downloadItem() {
        val url = selectedBookItem.epub_url
        val file = DeviceUtils.getAppDirectory(mContext)
        if (!file.exists()) {
            file.mkdirs()
        }
        val filePath = file.toString() + "/${selectedBookItem.title}.${selectedBookItem.format}"
        val request = Request(url, filePath)
        request.priority = Priority.HIGH
        request.enqueueAction = EnqueueAction.REPLACE_EXISTING
        request.tag = selectedBookItem.title + "/" + selectedBookItem.book_id + "/" + horizontalPosition + "/" + itemPosition

        val downloadID = fetch.enqueue(request)
    }

    private lateinit var selectedBookItem: ResponseModel.BooksItem

    private var horizontalPosition: Int = -1
    private var itemPosition: Int = -1
    private var itemTag: Int = -1

    override fun onItemClickListener(singleItem: ResponseModel.BooksItem, horizontalPosition: Int, itemPosition: Int, itemTag: Int) {
        PrintLog.debug("Khaleel", "horizontalPosition - $horizontalPosition - itemPosition - $itemPosition")
        selectedBookItem = singleItem
        this.horizontalPosition = horizontalPosition
        this.itemPosition = itemPosition
        this.itemTag = itemTag
        checkPermissionAndDownload()
    }

    private var mPage: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPage = arguments?.getInt(ARG_PAGE) ?: 0
    }

    lateinit var categoryAdapter: CategoryListAdapter

    lateinit var rvVerticalItem: RecyclerView
    lateinit var txtErrorMsg: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_page, container, false)

        rvVerticalItem = view.findViewById<View>(R.id.rvVerticalList) as RecyclerView
        txtErrorMsg = view.findViewById<View>(R.id.txtError) as TextView
        categoryAdapter = CategoryListAdapter(activity?.applicationContext, ArrayList(), this)


        rvVerticalItem.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rvVerticalItem.layoutManager = layoutManager
        rvVerticalItem.isNestedScrollingEnabled = true
        rvVerticalItem.adapter = categoryAdapter
        initApi()
        return view
    }

    private val tmpBookList: MutableList<ResponseModel.CategoryItemResponse> = mutableListOf()

    private fun initApi() {
        val booksList: MutableList<Any> = pultusORM.find(BookDB())
        PrintLog.debug("Khaleel", "BookList : $booksList")
        val ettuThogaiCall = SEAppUtil.getRetrofit().getEttuthogai()
        ettuThogaiCall.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    for (i in result.indices) {
                        if (result[i].books.size > 0) {
                            if (booksList.size > 0) {
                                for (books in result[i].books) {
                                    for (savedBooksIt in booksList) {
                                        val savedBook = savedBooksIt as BookDB
                                        if (savedBook.bookId == Integer.parseInt(books.book_id)) {
                                            books.status = AppConstants.STATUS_COMPLETED
                                        }
                                    }
                                }
                            }
                            tmpBookList.add(result[i])
                        }
                    }
                    PrintLog.debug("Khaleel", "List : $tmpBookList")
                    categoryAdapter.setItems(tmpBookList)
                }, {
                    rvVerticalItem.visibility = View.GONE
                    txtErrorMsg.visibility = View.VISIBLE
                    txtErrorMsg.text = getString(R.string.try_again_later)
                })
    }

    companion object {
        val ARG_PAGE = "ARG_PAGE"

        fun newInstance(page: Int): EttuThogaiFragment {
            val args = Bundle()
            args.putInt(ARG_PAGE, page)
            val fragment = EttuThogaiFragment()
            fragment.arguments = args
            return fragment
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
    }

    override fun onPauseDownload(id: Int) {
        fetch.pause(id)
    }

    override fun onResumeDownload(id: Int) {
        fetch.resume(id)
    }

    override fun onRemoveDownload(id: Int) {
        fetch.remove(id)
    }

    override fun onRetryDownload(id: Int) {
        fetch.retry(id)
    }

    private val fetchListener = object : AbstractFetchListener() {
        override fun onQueued(@NotNull download: Download, waitingOnNetwork: Boolean) {
            val titleID = download.tag!!.split("/")
            val horizPosition = Integer.parseInt(titleID[2])
            val itemPosition = Integer.parseInt(titleID[3])

            for (booksIt in tmpBookList) {
                for (books in booksIt.books) {
                    if (books.book_id == titleID[1]) {
                        tmpBookList[horizPosition].books[itemPosition].status = AppConstants.STATUS_QUEUED
                        categoryAdapter.updateItemStatus(tmpBookList[horizPosition].books[itemPosition], horizPosition, itemPosition)

                        val bookItem = BookDB()
                        bookItem.bookId = Integer.parseInt(titleID[1])
                        bookItem.title = titleID[0]
                        bookItem.bookUrl = download.file
                        bookItem.downloadStatus = AppConstants.STATUS_QUEUED
                        pultusORM.save(bookItem)
                    }
                }
            }
        }

        override fun onCompleted(@NotNull download: Download) {
            val titleID = download.tag!!.split("/")
            val horizPosition = Integer.parseInt(titleID[2])
            val itemPosition = Integer.parseInt(titleID[3])
            PrintLog.debug("Khaleel", "Tag : $horizPosition -- $itemPosition")


            for (booksIt in tmpBookList) {
                for (book in booksIt.books) {
                    if (book.book_id == titleID[1]) {
                        tmpBookList[horizPosition].books[itemPosition].status = AppConstants.STATUS_COMPLETED
                        categoryAdapter.updateItemStatus(tmpBookList[horizPosition].books[itemPosition], horizPosition, itemPosition)

                        Snackbar.make(rvVerticalItem, String.format(getString(R.string.download_complete), titleID[0]), Snackbar.LENGTH_LONG).show()

                        DeviceUtils.sendEventUpdate(mContext, broadcastManager, download.id.toLong(),
                                AppConstants.STATUS_DONE_CODE, download.progress, Integer.parseInt(titleID[1]),
                                download.downloaded, download.total, 1, titleID[0], true)

                        val condition: PultusORMCondition = PultusORMCondition.Builder()
                                .eq("bookId", titleID[1])
                                .build()

                        val updater: PultusORMUpdater = PultusORMUpdater.Builder()
                                .set("downloadStatus", AppConstants.STATUS_COMPLETED)
                                .condition(condition)
                                .build()
                        pultusORM.update(BookDB(), updater)
                    }
                }
            }
        }

        override fun onError(@NotNull download: Download) {
            val titleID = download.tag!!.split("/")
            val horizPosition = Integer.parseInt(titleID[2])
            val itemPosition = Integer.parseInt(titleID[3])
            PrintLog.debug("Khaleel", "Tag : ${horizPosition} -- ${itemPosition}")

            for (booksIt in tmpBookList) {
                for (book in booksIt.books) {
                    if (book.book_id == titleID[1]) {
                        tmpBookList[horizPosition].books[itemPosition].status = AppConstants.STATUS_ERROR
                        categoryAdapter.updateItemStatus(tmpBookList[horizPosition].books[itemPosition], horizPosition, itemPosition)

                        Snackbar.make(rvVerticalItem, String.format(getString(R.string.download_error), titleID[0]), Snackbar.LENGTH_LONG).show()

                        val condition: PultusORMCondition = PultusORMCondition.Builder()
                                .eq("bookId", titleID[1])
                                .build()

                        val updater: PultusORMUpdater = PultusORMUpdater.Builder()
                                .set("downloadStatus", AppConstants.STATUS_ERROR)
                                .condition(condition)
                                .build()
                        pultusORM.update(BookDB(), updater)
                    }
                }
            }
        }

        override fun onProgress(@NotNull download: Download, etaInMilliSeconds: Long, downloadedBytesPerSecond: Long) {
            val titleID = download.tag!!.split("/")

            DeviceUtils.sendEventUpdate(mContext, broadcastManager, download.id.toLong(),
                    AppConstants.STATUS_DOWNLOADING_CODE, download.progress, Integer.parseInt(titleID[1]),
                    download.downloaded, download.total, 1, titleID[0], true)
        }

        override fun onCancelled(@NotNull download: Download) {
            val titleID = download.tag!!.split("/")
            DeviceUtils.sendEventUpdate(mContext, broadcastManager, download.id.toLong(),
                    AppConstants.STATUS_REMOVED_CODE, download.progress, Integer.parseInt(titleID[1]),
                    download.downloaded, download.total, 1, titleID[0], true)
        }
    }
}