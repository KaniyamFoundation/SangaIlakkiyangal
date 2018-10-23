package com.jskaleel.sangaelakkiyangal.listeners

import com.jskaleel.sangaelakkiyangal.model.ResponseModel
import io.reactivex.Observable
import retrofit2.http.GET

interface ApiInterface {
    @GET("master/categorydb.json")
    fun getCategories(): Observable<List<ResponseModel.MainListResponse>>

    @GET("master/ettuthogai.json")
    fun getEttuthogai(): Observable<MutableList<ResponseModel.CategoryItemResponse>>

    @GET("master/pathinenkilkanakku.json")
    fun getPathinenkilkanakku(): Observable<MutableList<ResponseModel.CategoryItemResponse>>

    @GET("master/paththuppattu.json")
    fun getPaththuppattu(): Observable<MutableList<ResponseModel.CategoryItemResponse>>
}