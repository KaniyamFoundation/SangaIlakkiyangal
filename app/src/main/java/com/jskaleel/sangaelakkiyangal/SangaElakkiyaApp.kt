package com.jskaleel.sangaelakkiyangal

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.crashlytics.android.Crashlytics
import com.jskaleel.sangaelakkiyangal.utils.AppConstants
import com.jskhaleel.hellofreshtest.database.AppDataBase
import io.fabric.sdk.android.Fabric
import java.util.ArrayList


class SangaElakkiyaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())

        AppDataBase.getAppDatabase(this@SangaElakkiyaApp)
        initNotification()
    }

    private fun initNotification() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notificationChannels = ArrayList<NotificationChannel>()

            notificationChannels.add(NotificationChannel(AppConstants.DOWNLOAD_NOTIFICATION, "Downloads", NotificationManager.IMPORTANCE_LOW))

            notificationManager.createNotificationChannels(notificationChannels)
        }
    }
}