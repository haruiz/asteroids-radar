package com.udacity.asteroidradar.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.api.Network
import com.udacity.asteroidradar.api.getSeventhDay
import com.udacity.asteroidradar.api.getToday
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.models.PictureOfDay
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AsteroidsDatabase.getInstance(application)
    private val asteroidsRepository = AsteroidsRepository(database)

    private val _todayPicture = MutableLiveData<PictureOfDay>()
    val todayPicture: LiveData<PictureOfDay>
        get() = _todayPicture

    private val _query = MutableLiveData<String?>()
    val asteroids: LiveData<Array<Asteroid>> = Transformations.switchMap(_query){q ->
        loadAsteroids(q)
    }

    init {
        _query.value = null
        viewModelScope.launch {
            try {
                asteroidsRepository.refreshAsteroids()
                loadTodayPicture()
            } catch (e: Exception) {
                println("Exception refreshing data: $e.message")
            }
        }
    }

    private suspend fun loadTodayPicture() {
        _todayPicture.value = Network.NASANeoAPI.getTodayPicture()
    }

    fun changeQuery(q: String?){
        _query.value = q
    }

    fun loadAsteroids(q: String?): LiveData<Array<Asteroid>> {
        return when(q) {
            "week" -> asteroidsRepository.getAsteroidsByDateRange(getToday(), getSeventhDay())
            "today" -> asteroidsRepository.getAsteroidsByDateRange(getToday(), getSeventhDay())
            else -> asteroidsRepository.getAllAsteroids()
        }
    }

    private val _clickedAsteroidItem = MutableLiveData<Asteroid>()
    val clickedAsteroid
        get() = _clickedAsteroidItem
    fun onAsteroidClickEvent(asteroid: Asteroid) {
        _clickedAsteroidItem.value = asteroid
    }
    fun onAsteroidClickEventComplete() {
        _clickedAsteroidItem.value = null
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) return MainViewModel(app) as T
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}