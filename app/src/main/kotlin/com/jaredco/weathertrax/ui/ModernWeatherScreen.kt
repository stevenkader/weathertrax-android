package com.jaredco.weathertrax.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.jaredco.weathertrax.R
import com.jaredco.weathertrax.WeatherViewModel
import com.jaredco.weathertrax.util.WeatherIconMapper
import java.text.SimpleDateFormat
import java.util.*

// Modern color scheme - matching reference design
private val ModernDarkTop = Color(0xFF3A4A5A)
private val ModernDarkBottom = Color(0xFF2B3544)
private val ModernGold = Color(0xFFC9A961)
private val ModernWhite = Color(0xFFFFFFFF)
private val ModernGray = Color(0xFF888888)
private val ForecastBlue = Color(0xFF1E3A5C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernWeatherScreen(viewModel: WeatherViewModel) {
    val weatherState by viewModel.weatherState.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val savedLocations by viewModel.savedLocations.collectAsState()
    val useFahrenheit by viewModel.useFahrenheit.collectAsState()
    val showSearchDialog by viewModel.showSearchDialog.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var showLocationDropdown by remember { mutableStateOf(false) }
    var showInlineSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showGpsNameDialog by remember { mutableStateOf(false) }
    var showGpsSaveDialog by remember { mutableStateOf(false) }
    var gpsLatitude by remember { mutableStateOf(0.0) }
    var gpsLongitude by remember { mutableStateOf(0.0) }
    var isFromCurrentLocation by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            // Permission granted, get location
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        gpsLatitude = it.latitude
                        gpsLongitude = it.longitude
                        if (isFromCurrentLocation) {
                            // From Current Location dropdown - load weather and ask to save
                            viewModel.fetchWeather(it.latitude.toString(), it.longitude.toString())
                            showGpsSaveDialog = true
                        } else {
                            // From Add GPS Location menu - just ask for name
                            showGpsNameDialog = true
                        }
                    }
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "WeatherTrax",
                        color = ModernWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                actions = {
                    // Share icon
                    IconButton(onClick = {
                        weatherState?.let { weather ->
                            currentLocation?.let { location ->
                                weather.currentCondition?.let { current ->
                                    val tempUnit = if (useFahrenheit) "°F" else "°C"
                                    val temp = if (useFahrenheit) current.tempF else current.tempC
                                    val high = weather.weatherForecast?.firstOrNull()?.let {
                                        if (useFahrenheit) it.maxTempF else it.maxTempC
                                    } ?: "--"
                                    val low = weather.weatherForecast?.firstOrNull()?.let {
                                        if (useFahrenheit) it.minTempF else it.minTempC
                                    } ?: "--"

                                    val shareText = """
Weather in ${location.areaName}
Currently $temp$tempUnit - ${current.getWeatherDescription()}
High: $high$tempUnit | Low: $low$tempUnit
Humidity: ${current.humidity}%

Shared via WeatherTrax
                                    """.trimIndent()

                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        putExtra(Intent.EXTRA_SUBJECT, "Weather in ${location.areaName}")
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share weather via"))
                                }
                            }
                        }
                    }) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share",
                            tint = ModernWhite
                        )
                    }

                    // Support icon
                    IconButton(onClick = {
                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:support@jaredco.com?subject=${Uri.encode("WeatherTrax Support")}&body=${Uri.encode("Thank you for using WeatherTrax! Please describe your issue or question below:\n\n\n\n")}")
                        }
                        try {
                            context.startActivity(emailIntent)
                        } catch (e: Exception) {
                            // If no email app is available, ignore
                        }
                    }) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Support",
                            tint = ModernWhite
                        )
                    }

                    // Menu icon
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = ModernWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2B3544)
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(ModernDarkTop, ModernDarkBottom)
                    )
                )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Weather Content
                weatherState?.let { weather ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    weather.currentCondition?.let { current ->
                        // Top section - premium spacing and hierarchy
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                                // Weather icon - reduced bloom (additional 20% reduction)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val weatherCode = current.getIconCode()
                                    Image(
                                        painter = painterResource(
                                            if (weatherCode.isNotEmpty()) {
                                                WeatherIconMapper.getIconResource(weatherCode)
                                            } else {
                                                R.drawable.wsymbol_0001_sunny
                                            }
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier.size(105.dp),
                                        alpha = 1f
                                    )
                                }

                                // Reduced space after icon - begins unified current-weather group
                                Spacer(modifier = Modifier.height(8.dp))

                                // Location name with globe icon - positioned to the right (clickable dropdown)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(end = 16.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.CenterEnd)
                                            .width(280.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .align(Alignment.CenterEnd)
                                                .clickable { showLocationDropdown = !showLocationDropdown },
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = currentLocation?.areaName ?: "Select Location",
                                                fontSize = 19.sp,
                                                color = ModernWhite,
                                                fontWeight = FontWeight.Light
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(
                                                painter = painterResource(R.drawable.ic_globe),
                                                contentDescription = "Location",
                                                tint = ModernWhite,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Icon(
                                                imageVector = if (showLocationDropdown) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                                contentDescription = "Dropdown",
                                                tint = ModernWhite,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        // Dropdown menu - the fixed-width anchor keeps its right edge aligned with the arrow.
                                        DropdownMenu(
                                            expanded = showLocationDropdown,
                                            onDismissRequest = {
                                                showLocationDropdown = false
                                                showInlineSearch = false
                                                searchQuery = ""
                                            },
                                            offset = androidx.compose.ui.unit.DpOffset(x = 0.dp, y = 8.dp),
                                            modifier = Modifier
                                                .background(Color(0xFF2C3E50))
                                                .width(280.dp)
                                                .border(
                                                    width = 1.dp,
                                                    color = ModernGray.copy(alpha = 0.5f),
                                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                                                )
                                        ) {
                                        // Inline search if active
                                        if (showInlineSearch) {
                                            Column(
                                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                            ) {
                                                OutlinedTextField(
                                                    value = searchQuery,
                                                    onValueChange = {
                                                        searchQuery = it
                                                        if (it.length >= 2) {
                                                            viewModel.searchCity(it)
                                                        }
                                                    },
                                                    placeholder = { Text("Type city name...", color = ModernGray) },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        focusedTextColor = ModernWhite,
                                                        unfocusedTextColor = ModernWhite,
                                                        cursorColor = ModernGold,
                                                        focusedBorderColor = ModernGold,
                                                        unfocusedBorderColor = ModernGray
                                                    ),
                                                    singleLine = true
                                                )

                                                // Show search results
                                                if (searchResults.isNotEmpty()) {
                                                    searchResults.take(5).forEach { result ->
                                                        DropdownMenuItem(
                                                            text = {
                                                                Text(
                                                                    "${result.areaName}, ${result.country}",
                                                                    color = ModernWhite
                                                                )
                                                            },
                                                            onClick = {
                                                                viewModel.addLocation(result)
                                                                showLocationDropdown = false
                                                                showInlineSearch = false
                                                                searchQuery = ""
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        } else {
                                            // Current Location option
                                            DropdownMenuItem(
                                                text = {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(
                                                            painter = painterResource(R.drawable.ic_globe),
                                                            contentDescription = "Current Location",
                                                            tint = ModernGold,
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text("Current Location", color = ModernGold, fontWeight = FontWeight.Bold)
                                                    }
                                                },
                                                onClick = {
                                                    isFromCurrentLocation = true
                                                    showLocationDropdown = false

                                                    // Check for location permission
                                                    val hasPermission = ContextCompat.checkSelfPermission(
                                                        context,
                                                        Manifest.permission.ACCESS_FINE_LOCATION
                                                    ) == PackageManager.PERMISSION_GRANTED ||
                                                    ContextCompat.checkSelfPermission(
                                                        context,
                                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                                    ) == PackageManager.PERMISSION_GRANTED

                                                    if (hasPermission) {
                                                        // Get location directly
                                                        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                                                        try {
                                                            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                                                location?.let {
                                                                    gpsLatitude = it.latitude
                                                                    gpsLongitude = it.longitude
                                                                    viewModel.fetchWeather(it.latitude.toString(), it.longitude.toString())
                                                                    showGpsSaveDialog = true
                                                                }
                                                            }
                                                        } catch (e: SecurityException) {
                                                            e.printStackTrace()
                                                        }
                                                    } else {
                                                        // Request permission
                                                        locationPermissionLauncher.launch(
                                                            arrayOf(
                                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                                Manifest.permission.ACCESS_COARSE_LOCATION
                                                            )
                                                        )
                                                    }
                                                }
                                            )

                                            HorizontalDivider(color = ModernGray.copy(alpha = 0.3f))

                                            // Show saved locations
                                            savedLocations.forEach { location ->
                                                DropdownMenuItem(
                                                    text = {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(
                                                                location.areaName,
                                                                color = if (location == currentLocation) ModernGold else ModernWhite,
                                                                modifier = Modifier.weight(1f)
                                                            )
                                                            Row(
                                                                horizontalArrangement = Arrangement.End,
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                if (location == currentLocation) {
                                                                    Icon(
                                                                        Icons.Default.Check,
                                                                        contentDescription = "Selected",
                                                                        tint = ModernGold,
                                                                        modifier = Modifier.size(20.dp)
                                                                    )
                                                                    Spacer(modifier = Modifier.width(8.dp))
                                                                }
                                                                // Delete button (only show if there's more than one location)
                                                                if (savedLocations.size > 1) {
                                                                    IconButton(
                                                                        onClick = {
                                                                            viewModel.removeLocation(location)
                                                                        },
                                                                        modifier = Modifier.size(32.dp)
                                                                    ) {
                                                                        Icon(
                                                                            Icons.Default.Close,
                                                                            contentDescription = "Delete",
                                                                            tint = ModernGray,
                                                                            modifier = Modifier.size(18.dp)
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    },
                                                    onClick = {
                                                        viewModel.selectLocation(location)
                                                        showLocationDropdown = false
                                                    }
                                                )
                                            }

                                            HorizontalDivider(color = ModernGray.copy(alpha = 0.3f))

                                            // Add City option
                                            DropdownMenuItem(
                                                text = {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(
                                                            Icons.Default.Add,
                                                            contentDescription = "Add",
                                                            tint = ModernGold,
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text("Add City", color = ModernGold, fontWeight = FontWeight.Bold)
                                                    }
                                                },
                                                onClick = {
                                                    showInlineSearch = true
                                                }
                                            )
                                        }
                                    }
                                    }
                                }

                                // Tighter connection between location and temperature (unified group)
                                Spacer(modifier = Modifier.height(4.dp))

                                // Main content row: Left side (conditions) + Right side (temperature/condition)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    // Left side: Current conditions - ultra-tight spacing within unified group
                                    Column(
                                        modifier = Modifier.weight(0.6f)
                                    ) {
                                        // High and Low on same line
                                        Text(
                                            text = "H ${weather.weatherForecast?.firstOrNull()?.let {
                                                if (useFahrenheit) it.maxTempF else it.maxTempC
                                            } ?: "--"}°   L ${weather.weatherForecast?.firstOrNull()?.let {
                                                if (useFahrenheit) it.minTempF else it.minTempC
                                            } ?: "--"}°",
                                            fontSize = 16.sp,
                                            color = ModernGold,
                                            fontWeight = FontWeight.Medium,
                                            lineHeight = 20.sp
                                        )

                                        // Pulled closer to feel like single weather summary block
                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Humidity - ultra-tight spacing
                                        Text(
                                            text = "Humidity ${current.humidity}%",
                                            fontSize = 14.sp,
                                            color = ModernWhite,
                                            lineHeight = 18.sp
                                        )

                                        // Minimal gap within detail group
                                        Spacer(modifier = Modifier.height(1.dp))

                                        // Rain - feels connected to humidity
                                        val rainValue = weather.weatherForecast?.firstOrNull()?.getTotalPrecipitation() ?: "0.0"
                                        val rainUnit = if (useFahrenheit) "in" else "mm"
                                        val convertedRain = if (useFahrenheit) {
                                            // Convert mm to inches (1 mm = 0.0393701 inches)
                                            val rainFloat = rainValue.toFloatOrNull() ?: 0f
                                            String.format("%.1f", rainFloat * 0.0393701f)
                                        } else {
                                            rainValue
                                        }
                                        Text(
                                            text = "Rain $convertedRain $rainUnit",
                                            fontSize = 14.sp,
                                            color = ModernWhite,
                                            lineHeight = 18.sp
                                        )
                                    }

                                    // Right side: Hero temperature with condition text tightly grouped
                                    Column(
                                        modifier = Modifier.weight(0.4f),
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        Text(
                                            text = "${if (useFahrenheit) current.tempF else current.tempC}°",
                                            fontSize = 78.sp,
                                            fontWeight = FontWeight.Thin,
                                            color = ModernGold,
                                            lineHeight = 72.sp,
                                            letterSpacing = (-1).sp
                                        )
                                        // Minimal space between temp and condition
                                        Spacer(modifier = Modifier.height(0.dp))
                                        Text(
                                            text = current.getWeatherDescription(),
                                            fontSize = 15.sp,
                                            color = ModernWhite.copy(alpha = 0.85f),
                                            fontWeight = FontWeight.Normal,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }

                                // Reduced breathing room before updated timestamp (unified group continues)
                                Spacer(modifier = Modifier.height(10.dp))

                                // Updated timestamp with refresh button
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        val currentTime = remember {
                                            val calendar = Calendar.getInstance()
                                            val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
                                            timeFormat.format(calendar.time)
                                        }
                                        Text(
                                            text = "Updated $currentTime",
                                            fontSize = 13.sp,
                                            color = ModernWhite.copy(alpha = 0.7f),
                                            fontWeight = FontWeight.Normal
                                        )
                                    }

                                    // Live refresh button
                                    IconButton(
                                        onClick = {
                                            currentLocation?.let { location ->
                                                viewModel.fetchWeather(location.latitude, location.longitude)
                                            }
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_refresh),
                                            contentDescription = "Refresh weather",
                                            tint = ModernGold,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }

                        // Intentional separation before forecast (40% reduction from 20dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Forecast section - premium polish with better rain units
                        weather.weatherForecast?.take(5)?.let { forecasts ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(ForecastBlue.copy(alpha = 0.85f))
                                    .padding(horizontal = 14.dp, vertical = 12.dp)
                            ) {
                                // Day of week labels - reduced emphasis for secondary feel
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Spacer(modifier = Modifier.width(50.dp))
                                    forecasts.forEach { forecast ->
                                        Box(
                                            modifier = Modifier.weight(1f),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = getDayName(forecast.date),
                                                fontSize = 10.sp,
                                                color = ModernWhite.copy(alpha = 0.55f),
                                                fontWeight = FontWeight.Normal,
                                                letterSpacing = 0.4.sp
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Weather icons row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Spacer(modifier = Modifier.width(50.dp))
                                    forecasts.forEach { forecast ->
                                        Box(
                                            modifier = Modifier.weight(1f),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Image(
                                                painter = painterResource(
                                                    if (forecast.getIconCode().isNotEmpty()) {
                                                        WeatherIconMapper.getIconResource(forecast.getIconCode())
                                                    } else {
                                                        R.drawable.wsymbol_0001_sunny
                                                    }
                                                ),
                                                contentDescription = null,
                                                modifier = Modifier.size(42.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // High row - tighter internal spacing
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "H",
                                        fontSize = 11.sp,
                                        color = ModernWhite.copy(alpha = 0.5f),
                                        modifier = Modifier.width(50.dp),
                                        fontWeight = FontWeight.Normal
                                    )
                                    forecasts.forEach { forecast ->
                                        Text(
                                            text = "${if (useFahrenheit) forecast.maxTempF else forecast.maxTempC}°",
                                            fontSize = 13.sp,
                                            color = ModernWhite,
                                            modifier = Modifier.weight(1f),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                            fontWeight = FontWeight.Normal,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                // Low row - tight spacing within temp group
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "L",
                                        fontSize = 11.sp,
                                        color = ModernWhite.copy(alpha = 0.5f),
                                        modifier = Modifier.width(50.dp),
                                        fontWeight = FontWeight.Normal
                                    )
                                    forecasts.forEach { forecast ->
                                        Text(
                                            text = "${if (useFahrenheit) forecast.minTempF else forecast.minTempC}°",
                                            fontSize = 13.sp,
                                            color = ModernWhite,
                                            modifier = Modifier.weight(1f),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                            fontWeight = FontWeight.Normal,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Rain row - compact label with inline unit styling
                                val rainUnit = if (useFahrenheit) "in" else "mm"
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Rain",
                                        fontSize = 11.sp,
                                        color = ModernWhite.copy(alpha = 0.5f),
                                        modifier = Modifier.width(50.dp),
                                        fontWeight = FontWeight.Normal
                                    )
                                    forecasts.forEach { forecast ->
                                        val rainMm = forecast.getTotalPrecipitation()
                                        val rainDisplay = if (useFahrenheit) {
                                            // Convert mm to inches
                                            val rainFloat = rainMm.toFloatOrNull() ?: 0f
                                            String.format("%.1f", rainFloat * 0.0393701f)
                                        } else {
                                            rainMm
                                        }
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.Bottom
                                        ) {
                                            Text(
                                                text = rainDisplay,
                                                fontSize = 12.sp,
                                                color = ModernWhite.copy(alpha = 0.85f),
                                                fontWeight = FontWeight.Normal,
                                                lineHeight = 14.sp
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = rainUnit,
                                                fontSize = 9.sp,
                                                color = ModernWhite.copy(alpha = 0.5f),
                                                fontWeight = FontWeight.Normal,
                                                modifier = Modifier.padding(bottom = 1.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
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
                        color = ModernWhite,
                        fontSize = 18.sp
                    )
                }
            }
            }

            // Menu Dialog
            if (showMenu) {
                ModernMenuDialog(
                    useFahrenheit = useFahrenheit,
                    onToggleUnit = { viewModel.toggleTemperatureUnit() },
                    onDismiss = { showMenu = false }
                )
            }

            // Search Dialog (reuse from classic)
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

            // GPS Name Dialog (for Add GPS Location from menu)
            if (showGpsNameDialog) {
                GpsLocationNameDialog(
                    onConfirm = { name ->
                        viewModel.addGpsLocation(name, gpsLatitude, gpsLongitude)
                        showGpsNameDialog = false
                    },
                    onDismiss = {
                        showGpsNameDialog = false
                    }
                )
            }

            // GPS Save Dialog (for Current Location from dropdown)
            if (showGpsSaveDialog) {
                GpsSaveLocationDialog(
                    onSave = {
                        showGpsSaveDialog = false
                        showGpsNameDialog = true
                    },
                    onSkip = {
                        showGpsSaveDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun ModernMenuDialog(
    useFahrenheit: Boolean,
    onToggleUnit: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Temperature Unit Selection
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 16.dp)
                ) {
                    Text(
                        "Temperature Unit",
                        fontSize = 18.sp,
                        color = ModernWhite,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Fahrenheit option
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { if (!useFahrenheit) onToggleUnit() },
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                            color = if (useFahrenheit) ModernGold else Color(0xFF3A3A3A),
                            border = if (useFahrenheit) null else androidx.compose.foundation.BorderStroke(1.dp, ModernGray.copy(alpha = 0.3f))
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "°F",
                                    fontSize = 24.sp,
                                    color = if (useFahrenheit) Color(0xFF1A1A1A) else ModernWhite,
                                    fontWeight = if (useFahrenheit) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }

                        // Celsius option
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { if (useFahrenheit) onToggleUnit() },
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                            color = if (!useFahrenheit) ModernGold else Color(0xFF3A3A3A),
                            border = if (!useFahrenheit) null else androidx.compose.foundation.BorderStroke(1.dp, ModernGray.copy(alpha = 0.3f))
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "°C",
                                    fontSize = 24.sp,
                                    color = if (!useFahrenheit) Color(0xFF1A1A1A) else ModernWhite,
                                    fontWeight = if (!useFahrenheit) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {},
        containerColor = Color(0xFF2C2C2C),
        textContentColor = ModernWhite
    )
}

@Composable
fun MenuOption(
    icon: String,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = ModernGray.copy(alpha = 0.3f),
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = icon,
                    fontSize = 24.sp
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text,
            fontSize = 18.sp,
            color = ModernWhite
        )
    }
}

@Composable
fun SimpleMenuOption(
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text,
            fontSize = 18.sp,
            color = ModernWhite
        )
    }
}

@Composable
fun GpsSaveLocationDialog(
    onSave: () -> Unit,
    onSkip: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onSkip,
        title = {
            Text(
                "Save This Location?",
                color = ModernWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        },
        text = {
            Text(
                "Would you like to save this location to your list for quick access later?",
                color = ModernWhite,
                fontSize = 14.sp
            )
        },
        confirmButton = {
            TextButton(onClick = onSave) {
                Text("Save", color = ModernGold, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onSkip) {
                Text("Skip", color = ModernWhite)
            }
        },
        containerColor = Color(0xFF2C2C2C),
        textContentColor = ModernWhite
    )
}

@Composable
fun GpsLocationNameDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var locationName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Name This Location",
                color = ModernWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        },
        text = {
            Column {
                Text(
                    "Give this GPS location a name so you can easily find it later:",
                    color = ModernWhite,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = locationName,
                    onValueChange = { locationName = it },
                    placeholder = { Text("e.g., Home, Office, Cabin", color = ModernGray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ModernWhite,
                        unfocusedTextColor = ModernWhite,
                        cursorColor = ModernGold,
                        focusedBorderColor = ModernGold,
                        unfocusedBorderColor = ModernGray
                    ),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (locationName.isNotBlank()) {
                        onConfirm(locationName.trim())
                    }
                },
                enabled = locationName.isNotBlank()
            ) {
                Text("Save", color = if (locationName.isNotBlank()) ModernGold else ModernGray)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = ModernWhite)
            }
        },
        containerColor = Color(0xFF2C2C2C),
        textContentColor = ModernWhite
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
