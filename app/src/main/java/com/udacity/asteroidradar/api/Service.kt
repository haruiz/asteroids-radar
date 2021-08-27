package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.models.PictureOfDay
import com.udacity.asteroidradar.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

var okHttpClient = OkHttpClient().newBuilder()
    .connectTimeout(40, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

/**
 * A retrofit service to fetch a devbyte playlist.
 */
interface NASANeoService {
    @GET("/neo/rest/v1/feed")
    suspend fun fetchAsteroids(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String? = null,
        @Query("api_key") apiKey: String = Constants.API_KEY
    ): String

    @GET("/planetary/apod")
    suspend fun getTodayPicture(
        @Query("api_key") apiKey: String = Constants.API_KEY
    ): PictureOfDay
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
/**
 * Main entry point for network access. Call like `Network.devbytes.getPlaylist()`
 */
//singleton
object Network {
    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(Constants.BASE_URL)
        .build()

    val NASANeoAPI: NASANeoService by lazy {
        retrofit.create(NASANeoService::class.java)
    }
}
