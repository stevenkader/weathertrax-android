package com.jaredco.weathertrax

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jaredco.weathertrax.data.SavedLocation
import com.jaredco.weathertrax.data.SearchResult
import com.jaredco.weathertrax.data.WeatherResponse
import com.jaredco.weathertrax.network.WeatherApi
import com.jaredco.weathertrax.util.LocationPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val api: WeatherApi = Retrofit.Builder()
        .baseUrl("https://api.worldweatheronline.com/")
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .build()
        .create(WeatherApi::class.java)

    private val locationPrefs = LocationPreferences(application)

    private val _weatherState = MutableStateFlow<WeatherResponse?>(null)
    val weatherState: StateFlow<WeatherResponse?> = _weatherState

    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults: StateFlow<List<SearchResult>> = _searchResults

    private val _savedLocations = MutableStateFlow<List<SavedLocation>>(emptyList())
    val savedLocations: StateFlow<List<SavedLocation>> = _savedLocations

    private val _currentLocation = MutableStateFlow<SavedLocation?>(null)
    val currentLocation: StateFlow<SavedLocation?> = _currentLocation

    private val _useFahrenheit = MutableStateFlow(false)
    val useFahrenheit: StateFlow<Boolean> = _useFahrenheit

    private val _showSearchDialog = MutableStateFlow(false)
    val showSearchDialog: StateFlow<Boolean> = _showSearchDialog

    init {
        loadSavedLocations()
        _useFahrenheit.value = locationPrefs.useFahrenheit
    }

    private fun loadSavedLocations() {
        val locations = locationPrefs.getSavedLocations()
        _savedLocations.value = locations
        if (locations.isNotEmpty()) {
            val index = locationPrefs.currentLocationIndex.coerceIn(0, locations.size - 1)
            _currentLocation.value = locations[index]
            fetchWeather(locations[index].latitude, locations[index].longitude)
        }
    }

    fun searchCity(query: String) {
        viewModelScope.launch {
            try {
                val response = api.searchCity(query = query)
                _searchResults.value = response.results ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchWeather(lat: String, lon: String) {
        viewModelScope.launch {
            try {
                val response = api.getWeather(query = "$lat,$lon")
                _weatherState.value = response
                // Debug logging
                response.currentCondition?.let { current ->
                    println("WeatherTrax DEBUG - observation_time: ${current.observationTime}")
                    println("WeatherTrax DEBUG - localObsDateTime: ${current.localObsDateTime}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun selectLocation(location: SavedLocation) {
        _currentLocation.value = location
        val index = _savedLocations.value.indexOf(location)
        if (index >= 0) {
            locationPrefs.currentLocationIndex = index
        }
        fetchWeather(location.latitude, location.longitude)
    }

    fun addLocation(searchResult: SearchResult) {
        val newLocation = SavedLocation(
            areaName = searchResult.areaName,
            country = searchResult.country,
            region = searchResult.region,
            latitude = searchResult.latitude,
            longitude = searchResult.longitude
        )
        locationPrefs.addLocation(newLocation)
        loadSavedLocations()
        selectLocation(newLocation)
    }

    fun addGpsLocation(customName: String, latitude: Double, longitude: Double) {
        val newLocation = SavedLocation(
            areaName = customName,
            country = "",
            region = "",
            latitude = latitude.toString(),
            longitude = longitude.toString()
        )
        locationPrefs.addLocation(newLocation)
        loadSavedLocations()
        selectLocation(newLocation)
    }

    fun removeLocation(location: SavedLocation) {
        locationPrefs.removeLocation(location)
        loadSavedLocations()
    }

    fun toggleTemperatureUnit() {
        _useFahrenheit.value = !_useFahrenheit.value
        locationPrefs.useFahrenheit = _useFahrenheit.value
    }

    fun refreshCurrentLocation() {
        _currentLocation.value?.let { location ->
            fetchWeather(location.latitude, location.longitude)
        }
    }

    fun showSearchDialog() {
        _showSearchDialog.value = true
        _searchResults.value = emptyList()
    }

    fun hideSearchDialog() {
        _showSearchDialog.value = false
    }

    fun resetWeather() {
        _weatherState.value = null
    }
}
