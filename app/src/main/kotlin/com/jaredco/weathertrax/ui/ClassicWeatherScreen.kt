package com.jaredco.weathertrax.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jaredco.weathertrax.WeatherViewModel
import com.jaredco.weathertrax.data.SavedLocation
import com.jaredco.weathertrax.data.SearchResult
import com.jaredco.weathertrax.data.WeatherResponse
import com.jaredco.weathertrax.util.WeatherIconMapper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Classic WeatherTrax color scheme
private val ClassicBackground = Color(0xFF000000)
private val ClassicText = Color(0xFFFFFFFF)
private val ClassicGray = Color(0xFF333333)
private val ClassicLightGray = Color(0xFF666666)
private val ClassicYellow = Color(0xFFFFFF00)
private val ClassicBlue = Color(0xFF4A90E2)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassicWeatherScreen(viewModel: WeatherViewModel) {
    val weatherState by viewModel.weatherState.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val savedLocations by viewModel.savedLocations.collectAsState()
    val useFahrenheit by viewModel.useFahrenheit.collectAsState()
    val showSearchDialog by viewModel.showSearchDialog.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    var showMenu by remember { mutableStateOf(false) }
    var showLocationDropdown by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ClassicBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = ClassicBlue,
                shadowElevation = 4.dp
            ) {
                Text(
                    text = "WeatherTrax",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Location Dropdown
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = showLocationDropdown,
                    onExpandedChange = { showLocationDropdown = it }
                ) {
                    OutlinedTextField(
                        value = currentLocation?.toDisplayString() ?: "No location selected",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = ClassicText,
                            unfocusedTextColor = ClassicText,
                            focusedContainerColor = ClassicGray,
                            unfocusedContainerColor = ClassicGray,
                            focusedBorderColor = ClassicLightGray,
                            unfocusedBorderColor = ClassicLightGray
                        ),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showLocationDropdown) }
                    )

                    ExposedDropdownMenu(
                        expanded = showLocationDropdown,
                        onDismissRequest = { showLocationDropdown = false },
                        modifier = Modifier.background(ClassicGray)
                    ) {
                        savedLocations.forEach { location ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        location.toDisplayString(),
                                        color = ClassicText
                                    )
                                },
                                onClick = {
                                    viewModel.selectLocation(location)
                                    showLocationDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            // Weather Content
            weatherState?.let { weather ->
                WeatherContent(
                    weather = weather,
                    useFahrenheit = useFahrenheit,
                    modifier = Modifier.weight(1f)
                )
            } ?: run {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (savedLocations.isEmpty()) {
                            "Tap + to add a city"
                        } else {
                            "Loading weather..."
                        },
                        color = ClassicText,
                        fontSize = 18.sp
                    )
                }
            }

            // Bottom Navigation
            BottomNavigationBar(
                onAddCity = { viewModel.showSearchDialog() },
                onRefresh = { viewModel.refreshCurrentLocation() },
                onMenu = { showMenu = true }
            )
        }

        // Menu Dropdown
        if (showMenu) {
            MenuDialog(
                useFahrenheit = useFahrenheit,
                onToggleUnit = { viewModel.toggleTemperatureUnit() },
                onDismiss = { showMenu = false }
            )
        }

        // Search Dialog
        if (showSearchDialog) {
            SearchLocationDialog(
                searchResults = searchResults,
                onSearch = { query -> viewModel.searchCity(query) },
                onSelectLocation = { result, saveInList ->
                    if (saveInList) {
                        viewModel.addLocation(result)
                    } else {
                        viewModel.fetchWeather(result.latitude, result.longitude)
                    }
                    viewModel.hideSearchDialog()
                },
                onDismiss = { viewModel.hideSearchDialog() }
            )
        }
    }
}

@Composable
fun WeatherContent(
    weather: WeatherResponse,
    useFahrenheit: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Current Conditions
        weather.currentCondition?.let { current ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side - observation time and humidity
                Column {
                    val currentTime = remember {
                        val calendar = Calendar.getInstance()
                        val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
                        timeFormat.format(calendar.time)
                    }
                    Text(
                        text = "Observed at:    $currentTime",
                        color = ClassicText,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Humidity:         ${current.humidity}%",
                        color = ClassicText,
                        fontSize = 18.sp
                    )
                }

                // Center - large temperature
                Text(
                    text = "${if (useFahrenheit) current.tempF else current.tempC} ${if (useFahrenheit) "C" else "C"}",
                    color = ClassicYellow,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )

                // Right side - weather icon
                val weatherCode = current.getIconCode()
                Image(
                    painter = painterResource(
                        if (weatherCode.isNotEmpty()) {
                            WeatherIconMapper.getIconResource(weatherCode)
                        } else {
                            com.jaredco.weathertrax.R.drawable.wsymbol_0001_sunny
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 5-Day Forecast Grid
        weather.weatherForecast?.let { forecasts ->
            ForecastGrid(
                forecasts = forecasts.take(5),
                useFahrenheit = useFahrenheit
            )
        }
    }
}

@Composable
fun ForecastGrid(
    forecasts: List<com.jaredco.weathertrax.data.Forecast>,
    useFahrenheit: Boolean
) {
    Column {
        // Days row - with spacer at start for alignment
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Empty spacer to align with row labels below
            Spacer(modifier = Modifier.width(50.dp))

            forecasts.forEach { forecast ->
                val dayName = getDayName(forecast.date)
                Text(
                    text = dayName,
                    color = ClassicText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Weather icons row - with spacer at start
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Empty spacer to align with row labels
            Spacer(modifier = Modifier.width(50.dp))

            forecasts.forEachIndexed { index, forecast ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .background(Color(0xFF6699CC)),
                    contentAlignment = Alignment.Center
                ) {
                    val weatherCode = forecast.getIconCode()
                    // Always show an icon, use sunny as default if code is empty
                    Image(
                        painter = painterResource(
                            if (weatherCode.isNotEmpty()) {
                                WeatherIconMapper.getIconResource(weatherCode)
                            } else {
                                com.jaredco.weathertrax.R.drawable.wsymbol_0001_sunny
                            }
                        ),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(0.8f)
                    )
                }
                if (index < forecasts.size - 1) {
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // High temperatures
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "High",
                color = ClassicText,
                fontSize = 14.sp,
                modifier = Modifier.width(50.dp)
            )
            forecasts.forEachIndexed { index, forecast ->
                Text(
                    text = if (useFahrenheit) forecast.maxTempF else forecast.maxTempC,
                    color = ClassicText,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                if (index < forecasts.size - 1) {
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }

        // Low temperatures
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Low",
                color = ClassicText,
                fontSize = 14.sp,
                modifier = Modifier.width(50.dp)
            )
            forecasts.forEachIndexed { index, forecast ->
                Text(
                    text = if (useFahrenheit) forecast.minTempF else forecast.minTempC,
                    color = ClassicText,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                if (index < forecasts.size - 1) {
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }

        // Rainfall
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Rain*",
                color = ClassicText,
                fontSize = 14.sp,
                modifier = Modifier.width(50.dp)
            )
            forecasts.forEachIndexed { index, forecast ->
                val rainValue = if (forecast.precipMM.isNotEmpty()) {
                    try {
                        String.format("%.1f", forecast.precipMM.toFloat())
                    } catch (e: Exception) {
                        forecast.precipMM
                    }
                } else {
                    "0.0"
                }
                Text(
                    text = rainValue,
                    color = ClassicText,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                if (index < forecasts.size - 1) {
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Rain footnote
        Text(
            text = "* in millimeters",
            color = ClassicText,
            fontSize = 12.sp
        )
    }
}

@Composable
fun BottomNavigationBar(
    onAddCity: () -> Unit,
    onRefresh: () -> Unit,
    onMenu: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ClassicGray,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(onClick = onAddCity)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add City",
                    tint = ClassicText,
                    modifier = Modifier.size(32.dp)
                )
                Text("Add City", color = ClassicText, fontSize = 12.sp)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(onClick = onRefresh)
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = ClassicText,
                    modifier = Modifier.size(32.dp)
                )
                Text("Refresh City", color = ClassicText, fontSize = 12.sp)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(onClick = onMenu)
            ) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "Menu",
                    tint = ClassicText,
                    modifier = Modifier.size(32.dp)
                )
                Text("Menu", color = ClassicText, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun MenuDialog(
    useFahrenheit: Boolean,
    onToggleUnit: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Settings") },
        text = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onToggleUnit)
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (useFahrenheit) "Fahrenheit" else "Celsius")
                    Switch(
                        checked = useFahrenheit,
                        onCheckedChange = { onToggleUnit() }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        containerColor = ClassicGray,
        textContentColor = ClassicText
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchLocationDialog(
    searchResults: List<SearchResult>,
    onSearch: (String) -> Unit,
    onSelectLocation: (SearchResult, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedResult by remember { mutableStateOf<SearchResult?>(null) }
    var saveInList by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Find Location", color = ClassicText)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Enter city name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ClassicText,
                        unfocusedTextColor = ClassicText
                    )
                )

                Button(
                    onClick = { onSearch(searchQuery) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text("Search")
                }

                if (searchResults.isNotEmpty()) {
                    Text(
                        "Select location",
                        color = ClassicText,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = saveInList,
                            onCheckedChange = { saveInList = it }
                        )
                        Text("Save in the list", color = ClassicText)
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        items(searchResults) { result ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedResult = result
                                    }
                                    .padding(vertical = 8.dp)
                            ) {
                                RadioButton(
                                    selected = selectedResult == result,
                                    onClick = { selectedResult = result }
                                )
                                Text(
                                    "${result.areaName}, ${result.region}, ${result.country}",
                                    color = ClassicText,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedResult?.let { result ->
                        onSelectLocation(result, saveInList)
                    }
                },
                enabled = selectedResult != null
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = ClassicGray,
        textContentColor = ClassicText
    )
}

private fun getDayName(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = inputFormat.parse(dateString)
        val outputFormat = SimpleDateFormat("EEE", Locale.US)
        date?.let { outputFormat.format(it).uppercase() } ?: ""
    } catch (e: Exception) {
        ""
    }
}
