package com.jskaleel.sangaelakkiyangal

import android.app.Application
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import ninja.sakib.pultusorm.core.PultusORM


class SangaElakkiyaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
    }
}