package com.example.signals

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface AlphaVantageService {
    @GET("query")
    suspend fun getDaily(
        @Query("function") function: String = "TIME_SERIES_DAILY_ADJUSTED",
        @Query("symbol") symbol: String,
        @Query("apikey") apikey: String,
        @Query("outputsize") outputSize: String = "compact"
    ): Map<String, Any>
}

object ApiClient {
    fun create(): AlphaVantageService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.alphavantage.co/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        return retrofit.create(AlphaVantageService::class.java)
    }
}
