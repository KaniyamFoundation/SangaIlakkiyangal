package com.jskaleel.sangaelakkiyangal.model

import com.jskaleel.sangaelakkiyangal.BuildConfig
import com.jskaleel.sangaelakkiyangal.listeners.ApiInterface
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object SEAppUtil {
    fun getRetrofit(): ApiInterface {
        val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.URL_HOST)
                .build()
        return retrofit.create(ApiInterface::class.java)
    }

    lateinit var categoriesList: List<ResponseModel.MainListResponse>

    fun setCategories(result: List<ResponseModel.MainListResponse>) {
        SEAppUtil.categoriesList = result
    }

    fun getCategories(): List<ResponseModel.MainListResponse> {
        return SEAppUtil.categoriesList
    }
}