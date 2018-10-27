package com.jskaleel.sangaelakkiyangal.model

import com.jskaleel.sangaelakkiyangal.BuildConfig
import com.jskaleel.sangaelakkiyangal.listeners.ApiInterface
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class SEAppUtil {
    companion object {
        var categoriesList: List<ResponseModel.MainListResponse>? = null

        fun setCategories(result: List<ResponseModel.MainListResponse>) {
            categoriesList = result
        }

        fun getCategories(): List<ResponseModel.MainListResponse>? {
            return categoriesList
        }

        fun getRetrofit(): ApiInterface {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BuildConfig.URL_HOST)
                    .build()
            return retrofit.create(ApiInterface::class.java)
        }
    }
}