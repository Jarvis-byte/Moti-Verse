package com.example.quotify.HttpHandler

import com.example.quotify.Model.AllQuotesData
import com.example.quotify.Model.RandomQuotesData
import com.example.quotify.Model.TagQuotesData
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("/quotes")
    suspend fun getListQuotes(@Query("page") page: Int): Response<AllQuotesData>

    @GET("/quotes/random")
    suspend fun getRandomQuotes(@Query("limit") limit: Int): Response<RandomQuotesData>

    @GET("/quotes")
    suspend fun getTagsQuotes(@Query("tags") tags: String): Call<TagQuotesData>


}