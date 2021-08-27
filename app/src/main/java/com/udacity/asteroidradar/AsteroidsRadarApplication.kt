package com.udacity.asteroidradar

import android.app.Application
import android.os.Build
import androidx.work.*
import com.udacity.asteroidradar.utils.Constants
import com.udacity.asteroidradar.workers.DeleteOutdatedDataWorker
import com.udacity.asteroidradar.workers.UpdateDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AsteroidsRadarApplication : Application(){
    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        delayedInit()
    }

    private fun delayedInit() {
        applicationScope.launch {
            setupRecurringWork()
        }
    }

    private fun setupRecurringWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }.build()

        val repeatingRefreshRequest = PeriodicWorkRequestBuilder<UpdateDataWorker>(
            // once a day
            1,
            TimeUnit.DAYS
        ).setConstraints(constraints)
            .build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            Constants.REFRESH_WORKER_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // keep - will disregard the new request
            repeatingRefreshRequest
        )

        val repeatingDeleteRequest = PeriodicWorkRequestBuilder<DeleteOutdatedDataWorker>(
            // once a day
            1,
            TimeUnit.DAYS
        ).setConstraints(constraints)
            .build()
        WorkManager.getInstance().enqueueUniquePeriodicWork(
            Constants.DELETE_WORKER_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // keep - will disregard the new request
            repeatingDeleteRequest
        )
    }
}