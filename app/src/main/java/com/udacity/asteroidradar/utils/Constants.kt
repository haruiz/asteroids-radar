package com.udacity.asteroidradar.utils

import com.udacity.asteroidradar.BuildConfig

object Constants {
    const val REFRESH_WORKER_NAME: String = "refresh_worker"
    const val DELETE_WORKER_NAME: String = "refresh_worker"
    const val API_QUERY_DATE_FORMAT = "YYYY-MM-dd"
    const val DEFAULT_END_DATE_DAYS = 7
    const val BASE_URL = "https://api.nasa.gov/"
    const val API_KEY = BuildConfig.NASA_API_KEY
    const val DATABASE_NAME = "asteroids.db"
}