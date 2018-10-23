package com.jskaleel.sangaelakkiyangal.utils

import android.util.Log
import com.jskaleel.sangaelakkiyangal.BuildConfig

object PrintLog {

    fun debug(tag: String, str: String) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        if (str.length > 4000) {
            Log.d(tag, str.substring(0, 4000))
            debug(tag, str.substring(4000))
        } else {
            Log.d(tag, str)
        }
    }
}