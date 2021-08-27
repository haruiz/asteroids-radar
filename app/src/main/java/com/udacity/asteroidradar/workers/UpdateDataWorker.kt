package com.udacity.asteroidradar.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import retrofit2.HttpException

class UpdateDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val database = AsteroidsDatabase.getInstance(applicationContext)
        val repository = AsteroidsRepository(database)
        return try {
            repository.refreshAsteroids()
            Result.success()
        } catch (exception: HttpException) {
            Result.retry()
        }
    }
}