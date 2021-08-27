/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.api.Network
import com.udacity.asteroidradar.api.getSeventhDay
import com.udacity.asteroidradar.api.getToday
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabaseEntity
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.models.asDomainModel
import com.udacity.asteroidradar.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidsRepository(private val database: AsteroidsDatabase){

    // Transformations.map: convert liveData into other
//    val asteroids : LiveData<Array<Asteroid>> = Transformations.map(
//        database.asteroidsDao.getAsteroids()){
//            it.asDomainModel()
//        }
    fun getAllAsteroids(): LiveData<Array<Asteroid>> {
        return  Transformations.map(
            database.asteroidsDao.getAllAsteroids()){
            it.asDomainModel()
        }
    }

    fun getAsteroidsByDateRange(startDate: String, endDate: String): LiveData<Array<Asteroid>> {
        return  Transformations.map(
            database.asteroidsDao.getAsteroidsByDateRange(startDate, endDate)){
            it.asDomainModel()
        }
    }

    suspend fun refreshAsteroids(
        startDate: String = getToday(),
        endDate: String = getSeventhDay()
    ) {
        var asteroidList: ArrayList<Asteroid>
        withContext(Dispatchers.IO) {
            val apiResponse: String = Network.NASANeoAPI.fetchAsteroids(
                startDate, endDate, apiKey = Constants.API_KEY
            )
            val apiResponseJson = JSONObject(apiResponse)
            asteroidList = parseAsteroidsJsonResult(apiResponseJson)
            database.asteroidsDao.insertAll(*asteroidList.asDomainModel())
        }
    }


    suspend fun deleteAsteroidsFromToday() {
        withContext(Dispatchers.IO) {
            database.asteroidsDao.deleteAsteroidsFrom(getToday())
        }
    }

}
