# AGENTS.md

This file provides guidance to Codex (Codex.ai/code) when working with code in this repository.

## Project Overview

WeatherTrax is an Android weather application built with Jetpack Compose and Kotlin. Originally designed for Kindle (legacy `kindlet.properties` exists), the modern Android version uses the World Weather Online API to provide city search and weather forecasts.

## Build & Development Commands

### Building
```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Clean build
./gradlew clean
```

### Testing
```bash
# Run unit tests
./gradlew test

# Run instrumented tests (requires emulator/device)
./gradlew connectedAndroidTest
```

### Running
```bash
# Install debug build on connected device/emulator
./gradlew installDebug
```

## Architecture

### Technology Stack
- **UI Framework**: Jetpack Compose with Material3
- **Architecture Pattern**: MVVM (Model-View-ViewModel)
- **Networking**: Retrofit with SimpleXML converter
- **Image Loading**: Coil
- **Async**: Kotlin Coroutines with StateFlow

### Key Components

**WeatherViewModel** (`app/src/main/kotlin/com/jaredco/weathertrax/WeatherViewModel.kt`)
- Single ViewModel managing all app state
- Uses MutableStateFlow/StateFlow for reactive UI updates
- Directly instantiates Retrofit API client (no dependency injection)
- Key flows:
  - `weatherState`: Current weather data or null
  - `searchResults`: List of cities from search

**WeatherApi** (`app/src/main/kotlin/com/jaredco/weathertrax/network/WeatherApi.kt`)
- Retrofit interface for World Weather Online API
- Hardcoded API key: `39faf746e43d4cf0869135157251106`
- Two endpoints:
  - `searchCity`: Returns list of matching cities
  - `getWeather`: Fetches weather data by lat/lon coordinates
- All responses are XML format (using SimpleXML)

**Data Models** (`app/src/main/kotlin/com/jaredco/weathertrax/data/WeatherData.kt`)
- XML-annotated data classes using SimpleFramework XML
- `WeatherResponse`: Root response with current conditions and forecast
- `SearchResponse`: City search results
- All fields are mutable vars with default values due to XML parsing requirements

**UI Flow** (`app/src/main/kotlin/com/jaredco/weathertrax/ui/WeatherScreen.kt`)
1. User enters city name in search field
2. Tap "Search" → triggers `viewModel.searchCity()`
3. Select a city from results → calls `viewModel.fetchWeather(lat, lon)`
4. Weather details displayed with current conditions and 5-day forecast
5. "Back to Search" resets to search mode

### Data Flow Pattern

```
User Input → WeatherScreen → WeatherViewModel → WeatherApi (Retrofit)
                                    ↓
                              StateFlow updates
                                    ↓
                            WeatherScreen recomposes
```

The app uses a simple unidirectional data flow. There is no repository pattern or local caching - all data comes directly from network calls.

### Important Notes

- **No Dependency Injection**: Retrofit client is instantiated directly in ViewModel
- **No Error UI**: Network errors are only printed to console (see WeatherViewModel.kt:33, 44)
- **No State Persistence**: App state is lost on configuration changes/process death
- **Hardcoded API Key**: Weather API key is committed in source code (WeatherApi.kt:11, 19)
- **Dual Targeting**: While `kindlet.properties` references a Kindle Kindlet main class, the current codebase is a standard Android app with no Kindle-specific code

## Configuration

- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34
- **Kotlin**: 1.9.22
- **Compose Compiler**: 1.5.8
- **Namespace**: `com.jaredco.weathertrax`
