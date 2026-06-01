package com.jaredco.weathertrax.network

import com.jaredco.weathertrax.data.SearchResponse
import com.jaredco.weathertrax.data.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("premium/v1/search.ashx")
    suspend fun searchCity(
        @Query("key") key: String = "39faf746e43d4cf0869135157251106",
        @Query("q") query: String,
        @Query("num_of_results") numResults: Int = 20,
        @Query("format") format: String = "xml"
    ): SearchResponse

    @GET("premium/v1/weather.ashx")
    suspend fun getWeather(
        @Query("key") key: String = "39faf746e43d4cf0869135157251106",
        @Query("q") query: String, // format "lat,lon"
        @Query("format") format: String = "xml",
        @Query("num_of_days") numDays: Int = 5,
        @Query("tp") tp: Int = 24
    ): WeatherResponse
}
