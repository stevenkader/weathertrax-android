package com.jaredco.weathertrax.data

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "data", strict = false)
data class WeatherResponse(
    @field:Element(name = "current_condition", required = false)
    var currentCondition: CurrentCondition? = null,

    @field:ElementList(inline = true, entry = "weather", required = false)
    var weatherForecast: List<Forecast>? = null,

    @field:Element(name = "time_zone", required = false)
    var timeZone: String = ""
)

@Root(name = "current_condition", strict = false)
data class CurrentCondition(
    @field:Element(name = "observation_time", required = false)
    var observationTime: String = "",

    @field:Element(name = "localObsDateTime", required = false)
    var localObsDateTime: String = "",

    @field:Element(name = "temp_C", required = false)
    var tempC: String = "",

    @field:Element(name = "temp_F", required = false)
    var tempF: String = "",

    @field:Element(name = "humidity", required = false)
    var humidity: String = "",

    @field:Element(name = "pressure", required = false)
    var pressure: String = "",

    @field:Element(name = "cloudcover", required = false)
    var cloudCover: String = "",

    @field:Element(name = "windspeedKmph", required = false)
    var windSpeedKmph: String = "",

    @field:Element(name = "winddir16Point", required = false)
    var windDir16Point: String = "",

    @field:ElementList(inline = true, entry = "weatherDesc", required = false)
    var weatherDescList: List<WeatherDesc>? = null,

    @field:Element(name = "weatherCode", required = false)
    var weatherCode: String = "",

    @field:Element(name = "weatherIconUrl", required = false)
    var weatherIconUrl: String = ""
) {
    fun getIconCode(): String = weatherCode

    fun getWeatherDescription(): String {
        return weatherDescList?.firstOrNull()?.value ?: ""
    }

    fun getFormattedObservationTime(): String {
        // Use localObsDateTime if available, otherwise use observation_time
        return if (localObsDateTime.isNotEmpty()) {
            // localObsDateTime format is typically "2025-05-26 12:44 PM"
            // Extract just the time portion
            val timePart = localObsDateTime.substringAfterLast(" ", "")
            val timeWithoutAMPM = localObsDateTime.substringBeforeLast(" ", "")
            val justTime = timeWithoutAMPM.substringAfterLast(" ", "")
            if (justTime.isNotEmpty() && timePart.matches(Regex("(AM|PM)"))) {
                "$justTime $timePart"
            } else if (observationTime.isNotEmpty()) {
                observationTime
            } else {
                localObsDateTime
            }
        } else {
            observationTime
        }
    }
}

@Root(name = "weatherDesc", strict = false)
data class WeatherDesc(
    @field:Element(name = "value", required = false)
    var value: String = ""
)

@Root(name = "weather", strict = false)
data class Forecast(
    @field:Element(name = "date", required = false)
    var date: String = "",

    @field:Element(name = "maxtempC", required = false)
    var maxTempC: String = "",

    @field:Element(name = "mintempC", required = false)
    var minTempC: String = "",

    @field:Element(name = "maxtempF", required = false)
    var maxTempF: String = "",

    @field:Element(name = "mintempF", required = false)
    var minTempF: String = "",

    @field:Element(name = "precipMM", required = false)
    var precipMM: String = "",

    @field:ElementList(inline = true, entry = "hourly", required = false)
    var hourly: List<HourlyForecast>? = null,

    @field:Element(name = "weatherCode", required = false)
    var weatherCode: String = "",

    @field:Element(name = "weatherIconUrl", required = false)
    var weatherIconUrl: String = ""
) {
    // Get the weather code from the first hourly forecast if main weatherCode is empty
    fun getIconCode(): String {
        return if (weatherCode.isNotEmpty()) {
            weatherCode
        } else {
            hourly?.firstOrNull()?.weatherCode ?: ""
        }
    }

    // Get wind data from hourly forecast
    fun getWindSpeed(): String {
        return hourly?.firstOrNull()?.windSpeedKmph ?: ""
    }

    fun getWindDirection(): String {
        return hourly?.firstOrNull()?.windDir16Point ?: ""
    }

    // Get total precipitation - try main field first, then sum hourly data
    fun getTotalPrecipitation(): String {
        // If main precipMM field has data, use it
        if (precipMM.isNotEmpty() && precipMM != "0.0") {
            return precipMM
        }

        // Otherwise, try to sum hourly precipitation
        val hourlyPrecip = hourly?.mapNotNull {
            it.precipMM.toFloatOrNull()
        }?.sum() ?: 0f

        return if (hourlyPrecip > 0) {
            String.format("%.1f", hourlyPrecip)
        } else {
            precipMM.ifEmpty { "0.0" }
        }
    }
}

@Root(name = "hourly", strict = false)
data class HourlyForecast(
    @field:Element(name = "weatherCode", required = false)
    var weatherCode: String = "",

    @field:Element(name = "weatherIconUrl", required = false)
    var weatherIconUrl: String = "",

    @field:Element(name = "windspeedKmph", required = false)
    var windSpeedKmph: String = "",

    @field:Element(name = "winddir16Point", required = false)
    var windDir16Point: String = "",

    @field:Element(name = "precipMM", required = false)
    var precipMM: String = "",

    @field:Element(name = "chanceofrain", required = false)
    var chanceOfRain: String = ""
)

@Root(name = "search_api", strict = false)
data class SearchResponse(
    @field:ElementList(inline = true, entry = "result", required = false)
    var results: List<SearchResult>? = null
)

@Root(name = "result", strict = false)
data class SearchResult(
    @field:Element(name = "areaName", required = false)
    var areaName: String = "",

    @field:Element(name = "country", required = false)
    var country: String = "",

    @field:Element(name = "region", required = false)
    var region: String = "",

    @field:Element(name = "latitude", required = false)
    var latitude: String = "",

    @field:Element(name = "longitude", required = false)
    var longitude: String = ""
)

/**
 * Represents a saved location for quick access
 */
data class SavedLocation(
    val areaName: String,
    val country: String,
    val region: String,
    val latitude: String,
    val longitude: String
) {
    fun toDisplayString(): String {
        return if (region.isNotEmpty() && region != areaName) {
            "$areaName($region) $country"
        } else {
            "$areaName, $country"
        }
    }
}
