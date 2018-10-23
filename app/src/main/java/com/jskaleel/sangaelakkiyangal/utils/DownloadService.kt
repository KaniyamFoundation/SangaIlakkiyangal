package com.jskaleel.sangaelakkiyangal.utils

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DownloadService : BroadcastReceiver() {
    private var mDownloadManager: DownloadManager? = null

    override fun onReceive(context: Context, intent: Intent) {
        mDownloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == intent.action) {

            val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
            val query = DownloadManager.Query()
            query.setFilterById(downloadId)
            val c = mDownloadManager!!.query(query)
            if (c.moveToFirst()) {
                val columnStatus = c.getColumnIndex(DownloadManager.COLUMN_STATUS)
                when (c.getInt(columnStatus)) {
                    DownloadManager.ERROR_HTTP_DATA_ERROR -> sendBroadcastMessage(context, downloadId, "FAILED")
                    DownloadManager.STATUS_SUCCESSFUL -> sendBroadcastMessage(context, downloadId, "SUCCESS")
                    DownloadManager.STATUS_FAILED -> sendBroadcastMessage(context, downloadId, "FAILED")
                }
            }
        }
    }

    private fun sendBroadcastMessage(context: Context, downloadId: Long, status: String) {
        PrintLog.debug("Download", "Khaleel : Status......$status")
        /*val mp3DownloadDB = DbUtils.getMp3Item(DbUtils.DOWNLOAD_ID, "" + downloadId)
        if (mp3DownloadDB != null) {
            mp3DownloadDB!!.setDownStatus(status)
            mp3DownloadDB!!.save()

            val broadcastIntent = Intent(DOWNLOAD_COMPLETED)
            broadcastIntent.putExtra(DbUtils.PUB_DATE, mp3DownloadDB!!.getPubDate())
            broadcastIntent.putExtra(DbUtils.DOWNLOAD_ID, downloadId)
            broadcastIntent.putExtra(DbUtils.STATUS, status)
            LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent)
        }*/
    }

    companion object {
        val DOWNLOAD_COMPLETED = "download_completed"
    }
}