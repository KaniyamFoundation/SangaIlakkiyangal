package com.jskaleel.sangaelakkiyangal.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jskaleel.sangaelakkiyangal.listeners.ActionListener
import com.tonyodev.fetch2.Fetch
import com.tonyodev.fetch2.FetchConfiguration
import com.tonyodev.fetch2.HttpUrlConnectionDownloader
import com.tonyodev.fetch2.NetworkType
import com.tonyodev.fetch2core.Downloader

abstract class BaseActivity : AppCompatActivity(), ActionListener {

    protected lateinit var fetch: Fetch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val fetchConfiguration = FetchConfiguration.Builder(applicationContext)
                .enableRetryOnNetworkGain(true)
                .setDownloadConcurrentLimit(1)
                .setGlobalNetworkType(NetworkType.ALL)
                .setNamespace("SANGA_ELAKKIYAM")
                .setProgressReportingInterval(2000L)
                .setHttpDownloader(HttpUrlConnectionDownloader(Downloader.FileDownloaderType.SEQUENTIAL))
                .build()
        Fetch.setDefaultInstanceConfiguration(fetchConfiguration)

        fetch = Fetch.getInstance(fetchConfiguration)
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
}