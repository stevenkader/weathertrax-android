package com.jaredco.weathertrax.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.jaredco.weathertrax.WeatherViewModel
import com.jaredco.weathertrax.data.Forecast
import com.jaredco.weathertrax.data.WeatherResponse

@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.collectAsState()
    val weatherState by viewModel.weatherState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "WeatherTrax",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Enter City Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { viewModel.searchCity(searchQuery) },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Search")
        }

        if (weatherState == null) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(searchResults) { city ->
                    TextButton(
                        onClick = { viewModel.fetchWeather(city.latitude, city.longitude) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("${city.areaName}, ${city.region}, ${city.country}")
                    }
                }
            }
        } else {
            WeatherDetails(weatherState!!)
            Button(
                onClick = { viewModel.resetWeather() },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Back to Search")
            }
        }
    }
}

@Composable
fun WeatherDetails(weather: WeatherResponse) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        weather.currentCondition?.let { curr ->
            Text("Observed at: ${curr.observationTime}")
            Text("Humidity: ${curr.humidity}%")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${curr.tempC}°C",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                AsyncImage(
                    model = curr.weatherIconUrl,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            weather.weatherForecast?.forEach { forecast ->
                ForecastItem(forecast)
            }
        }
    }
}

@Composable
fun ForecastItem(forecast: Forecast) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(forecast.date.takeLast(5)) // Simplified date
        AsyncImage(
            model = forecast.weatherIconUrl,
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )
        Text("H: ${forecast.maxTempC}")
        Text("L: ${forecast.minTempC}")
    }
}
