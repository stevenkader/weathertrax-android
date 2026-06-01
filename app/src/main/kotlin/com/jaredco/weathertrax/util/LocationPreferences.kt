package com.jaredco.weathertrax.util

import android.content.Context
import android.content.SharedPreferences
import com.jaredco.weathertrax.data.SavedLocation
import org.json.JSONArray
import org.json.JSONObject

class LocationPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("weathertrax_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SAVED_LOCATIONS = "saved_locations"
        private const val KEY_USE_FAHRENHEIT = "use_fahrenheit"
        private const val KEY_CURRENT_LOCATION_INDEX = "current_location_index"
    }

    fun getSavedLocations(): List<SavedLocation> {
        val json = prefs.getString(KEY_SAVED_LOCATIONS, null) ?: return emptyList()
        val locations = mutableListOf<SavedLocation>()

        try {
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                locations.add(
                    SavedLocation(
                        areaName = obj.getString("areaName"),
                        country = obj.getString("country"),
                        region = obj.optString("region", ""),
                        latitude = obj.getString("latitude"),
                        longitude = obj.getString("longitude")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return locations
    }

    fun saveLocations(locations: List<SavedLocation>) {
        val jsonArray = JSONArray()
        locations.forEach { location ->
            val obj = JSONObject().apply {
                put("areaName", location.areaName)
                put("country", location.country)
                put("region", location.region)
                put("latitude", location.latitude)
                put("longitude", location.longitude)
            }
            jsonArray.put(obj)
        }

        prefs.edit().putString(KEY_SAVED_LOCATIONS, jsonArray.toString()).apply()
    }

    fun addLocation(location: SavedLocation) {
        val locations = getSavedLocations().toMutableList()
        // Avoid duplicates
        if (!locations.any { it.latitude == location.latitude && it.longitude == location.longitude }) {
            locations.add(location)
            saveLocations(locations)
        }
    }

    fun removeLocation(location: SavedLocation) {
        val locations = getSavedLocations().toMutableList()
        locations.removeAll { it.latitude == location.latitude && it.longitude == location.longitude }
        saveLocations(locations)
    }

    var useFahrenheit: Boolean
        get() = prefs.getBoolean(KEY_USE_FAHRENHEIT, false)
        set(value) = prefs.edit().putBoolean(KEY_USE_FAHRENHEIT, value).apply()

    var currentLocationIndex: Int
        get() = prefs.getInt(KEY_CURRENT_LOCATION_INDEX, 0)
        set(value) = prefs.edit().putInt(KEY_CURRENT_LOCATION_INDEX, value).apply()
}
