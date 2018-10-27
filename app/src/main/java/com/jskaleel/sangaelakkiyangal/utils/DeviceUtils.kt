package com.jskaleel.sangaelakkiyangal.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import com.jskaleel.sangaelakkiyangal.R
import com.jskaleel.sangaelakkiyangal.ui.activities.HomeActivity
import java.io.File
import java.text.DecimalFormat


object DeviceUtils {

    fun getAppDirectory(context: Context): File {
        return File(context.getExternalFilesDir(null)!!.toString() + "/books")
    }


    fun isNetworkAvailable(context: Context): Boolean {

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun sendEventUpdate(context: Context, broadcastManager: LocalBroadcastManager?, id: Long,
                        status: Int, progress: Int, bookId: Int, downloadedBytes: Long, fileSize: Long,
                        isBook: Int, bookName: String, showToast: Boolean) {
        try {
            if (broadcastManager == null) {
                return
            }
            //===============================
            if (isBook == 1) {
                when (status) {
                    AppConstants.STATUS_DOWNLOADING_CODE, AppConstants.STATUS_QUEUED_CODE, AppConstants.STATUS_PAUSED_CODE, AppConstants.CUSTOM_STATUS_RESUME_CODE -> {
                        val builder = updateNotification(context, bookName, progress, bookId.toLong(), downloadedBytes, fileSize)
                        val mNotificationManager1 = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        mNotificationManager1.notify(AppConstants.NOTIFICATION_ID, builder.build())
                    }
                    AppConstants.STATUS_NOT_QUEUED_CODE -> cancelOnGoingNotification(context)
                    AppConstants.STATUS_ERROR_CODE -> {
                        cancelOnGoingNotification(context)
                        showErrorNotification(context, context.getString(R.string.download_down_failed), context.getString(R.string.global_something_went_wrong), bookId.toLong())
                    }
                    AppConstants.STATUS_DONE_CODE -> {
                        cancelOnGoingNotification(context)
                        val builder1 = updateMessageNotification(context, bookName, bookId.toLong())
                        val mNotificationManager4 = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        mNotificationManager4.notify(AppConstants.NOTIFICATION_ID, builder1.build())
                    }
                    AppConstants.STATUS_REMOVED_CODE -> {
                        cancelOnGoingNotification(context)
                        val mNotificationManager5 = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        mNotificationManager5.cancel(AppConstants.NOTIFICATION_ID)
                    }
                }

                if (showToast) {
                    val intent = Intent(AppConstants.DOWNLOAD_VIDEO_COMPLETED)
                    intent.putExtra(AppConstants.DOWNLOAD_STATUS, status)
                    broadcastManager.sendBroadcast(intent)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun cancelOnGoingNotification(context: Context) {
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.cancel(AppConstants.NOTIFICATION_ID)
    }

    private fun updateNotification(context: Context, bookName: String, progress: Int, bookId: Long, downloadedBytes: Long, fileSize: Long): NotificationCompat.Builder {
        val largeIcon = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
        val builder1 = NotificationCompat.Builder(context, AppConstants.DOWNLOAD_NOTIFICATION)
        builder1.setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle(bookName)
                .setContentIntent(getPendingIntent(context, AppConstants.NOTIFICATION_ID))
                .setColor(ContextCompat.getColor(context, R.color.primary_grey))
                .setLargeIcon(largeIcon)
                .setShowWhen(false)
                .setSmallIcon(R.drawable.ic_notification_icon)


        val dec = DecimalFormat("0.0")
        builder1.setProgress(100, progress, false)
        builder1.addAction(getCancelActionButton(context, bookId))
        val pa_fSize = dec.format(fileSize / 1048576) + "MB"
        val pa_dSize = dec.format(downloadedBytes / 1048576) + "MB"
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            builder1.setContentText(progress.toString() + "% (" + pa_dSize + "/" + pa_fSize + ")")
        } else {
            builder1.setStyle(NotificationCompat.BigTextStyle().setSummaryText(progress.toString() + "% (" + pa_dSize + "/" + pa_fSize + ")"))
        }
        return builder1
    }

    private fun getCancelActionButton(context: Context, bookId: Long): NotificationCompat.Action {
        val intent = Intent(context, DownloadNotificationIntentReceiver::class.java)
        intent.action = AppConstants.ACTION_CANCEL_PLAYBACK
        intent.setPackage(context.packageName)
        val pendingIntent = PendingIntent.getBroadcast(context,
                bookId.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat.Action.Builder(R.drawable.ic_notification_cancel1,
                "Cancel", pendingIntent).build()
    }

    private fun getPendingIntent(context: Context, requestCode: Int): PendingIntent? {

        val contentIntent = Intent(context, HomeActivity::class.java)

        contentIntent.putExtra(AppConstants.OPEN_SAVED_VIDEO_PAGE, true)
        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addNextIntent(contentIntent)
        return stackBuilder.getPendingIntent(requestCode, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun updateMessageNotification(context: Context, bookName: String, bookId: Long): NotificationCompat.Builder {
        val largeIcon = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)

        val builder1 = NotificationCompat.Builder(context, AppConstants.DOWNLOAD_NOTIFICATION)
        builder1.setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                .setContentTitle(bookName + " ${context.getString(R.string.download_complete)}")
                .setContentIntent(getPendingIntent(context, bookId.toInt()))
                .setShowWhen(true)
                .setAutoCancel(true)
                .setLargeIcon(largeIcon)
                .setColor(ContextCompat.getColor(context, R.color.primary_grey))
                .setSmallIcon(R.drawable.ic_notification_icon)
        return builder1
    }

    fun showErrorNotification(context: Context, errorTitle: String, errorMessage: String, bookId: Long) {
        val largeIcon = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)

        val builder1 = NotificationCompat.Builder(context, AppConstants.DOWNLOAD_NOTIFICATION)
        builder1.setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                .setContentTitle(errorTitle)
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(errorMessage))
                .setContentText(errorMessage)
                .setContentIntent(getPendingIntent(context, bookId.toInt()))
                .setShowWhen(true)
                .setAutoCancel(true)
                .setLargeIcon(largeIcon)
                .setColor(ContextCompat.getColor(context, R.color.primary_grey))
                .setSmallIcon(R.drawable.ic_notification_icon)
        val mNotificationManager4 = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager4.notify(AppConstants.NOTIFICATION_ID, builder1.build())
    }

    fun calculateNoOfColumns(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        return (dpWidth / 180).toInt()
    }
}