package com.jskaleel.sangaelakkiyangal.utils

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


/**
 * Created by nasrudeen on 26/9/17.
 */

class DownloadNotificationIntentReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        when (action) {
            AppConstants.ACTION_CANCEL_PLAYBACK -> {
                val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                mNotificationManager.cancel(AppConstants.NOTIFICATION_ID)
            }
        }
    }
}