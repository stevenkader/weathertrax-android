package com.jaredco.weathertrax.util

import com.jaredco.weathertrax.R

object WeatherIconMapper {
    /**
     * Maps World Weather Online weather codes to local drawable resources.
     * Based on the wsymbol naming convention from the original app.
     */
    fun getIconResource(weatherCode: String): Int {
        return when (weatherCode) {
            "113" -> R.drawable.wsymbol_0001_sunny  // Sunny/Clear
            "116" -> R.drawable.wsymbol_0002_sunny_intervals  // Partly cloudy
            "119" -> R.drawable.wsymbol_0003_white_cloud  // Cloudy
            "122" -> R.drawable.wsymbol_0004_black_low_cloud  // Overcast
            "143" -> R.drawable.wsymbol_0006_mist  // Mist
            "248" -> R.drawable.wsymbol_0007_fog  // Fog
            "260" -> R.drawable.wsymbol_0007_fog  // Freezing fog
            "176" -> R.drawable.wsymbol_0009_light_rain_showers  // Patchy rain possible
            "263" -> R.drawable.wsymbol_0009_light_rain_showers  // Patchy light drizzle
            "266" -> R.drawable.wsymbol_0009_light_rain_showers  // Light drizzle
            "281" -> R.drawable.wsymbol_0009_light_rain_showers  // Freezing drizzle
            "284" -> R.drawable.wsymbol_0010_heavy_rain_showers  // Heavy freezing drizzle
            "293" -> R.drawable.wsymbol_0017_cloudy_with_light_rain  // Patchy light rain
            "296" -> R.drawable.wsymbol_0017_cloudy_with_light_rain  // Light rain
            "299" -> R.drawable.wsymbol_0010_heavy_rain_showers  // Moderate rain at times
            "302" -> R.drawable.wsymbol_0010_heavy_rain_showers  // Moderate rain
            "305" -> R.drawable.wsymbol_0018_cloudy_with_heavy_rain  // Heavy rain at times
            "308" -> R.drawable.wsymbol_0018_cloudy_with_heavy_rain  // Heavy rain
            "311" -> R.drawable.wsymbol_0021_cloudy_with_sleet  // Light freezing rain
            "314" -> R.drawable.wsymbol_0021_cloudy_with_sleet  // Moderate or heavy freezing rain
            "317" -> R.drawable.wsymbol_0013_sleet_showers  // Light sleet
            "320" -> R.drawable.wsymbol_0013_sleet_showers  // Moderate or heavy sleet
            "323" -> R.drawable.wsymbol_0011_light_snow_showers  // Patchy light snow
            "326" -> R.drawable.wsymbol_0011_light_snow_showers  // Light snow
            "329" -> R.drawable.wsymbol_0012_heavy_snow_showers  // Patchy moderate snow
            "332" -> R.drawable.wsymbol_0012_heavy_snow_showers  // Moderate snow
            "335" -> R.drawable.wsymbol_0020_cloudy_with_heavy_snow  // Patchy heavy snow
            "338" -> R.drawable.wsymbol_0020_cloudy_with_heavy_snow  // Heavy snow
            "350" -> R.drawable.wsymbol_0013_sleet_showers  // Ice pellets
            "353" -> R.drawable.wsymbol_0009_light_rain_showers  // Light rain shower
            "356" -> R.drawable.wsymbol_0010_heavy_rain_showers  // Moderate or heavy rain shower
            "359" -> R.drawable.wsymbol_0018_cloudy_with_heavy_rain  // Torrential rain shower
            "362" -> R.drawable.wsymbol_0013_sleet_showers  // Light sleet showers
            "365" -> R.drawable.wsymbol_0013_sleet_showers  // Moderate or heavy sleet showers
            "368" -> R.drawable.wsymbol_0011_light_snow_showers  // Light snow showers
            "371" -> R.drawable.wsymbol_0012_heavy_snow_showers  // Moderate or heavy snow showers
            "374" -> R.drawable.wsymbol_0014_light_hail_showers  // Light showers of ice pellets
            "377" -> R.drawable.wsymbol_0015_heavy_hail_showers  // Moderate or heavy showers of ice pellets
            "386" -> R.drawable.wsymbol_0016_thundery_showers  // Patchy light rain with thunder
            "389" -> R.drawable.wsymbol_0024_thunderstorms  // Moderate or heavy rain with thunder
            "392" -> R.drawable.wsymbol_0027_light_snow_showers_night  // Patchy light snow with thunder
            "395" -> R.drawable.wsymbol_0028_heavy_snow_showers_night  // Moderate or heavy snow with thunder
            else -> R.drawable.wsymbol_0001_sunny  // Default to sunny
        }
    }
}
