package com.example.practica4

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

import androidx.core.view.WindowCompat
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.practica4.data.WeatherModel
import com.example.practica4.screens.DialogSearch
import com.example.practica4.screens.MianCard
import com.example.practica4.screens.TabLayout
import com.example.practica4.ui.theme.Practica4Theme
import org.json.JSONObject
import androidx.compose.ui.platform.LocalContext
import java.util.Locale
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.example.practica4.data.FavoriteCity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import org.json.JSONArray
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.core.content.ContextCompat
import com.example.practica4.utils.translateText

// Ключ API для сервиса погоды
const val API_KEY = "94f2f552328043b8965154019251310"

class MainActivity : ComponentActivity() {

    // Переменные для работы с геолокацией
    private lateinit var locationPermissionRequest: ActivityResultLauncher<Array<String>>
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Ключи для SharedPreferences
    private val searchHistoryKey = "search_history"
    private val favoritesKey = "favorite_cities"

    // Состояния данных
    private lateinit var daysList: MutableState<List<WeatherModel>>
    private lateinit var currentDay: MutableState<WeatherModel>
    private val shouldRequestLocation = mutableStateOf(false)

    // Код запроса для Activity избранных
    private companion object {
        const val FAVORITES_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Включение edge-to-edge отображения
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, true)

        // Инициализация клиента геолокации
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Очистка данных при первом запуске приложения
        val prefs = getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
        val isFirstRun = prefs.getBoolean("is_first_run", true)
        if (isFirstRun) {
            prefs.edit().clear().apply()
            prefs.edit().putBoolean("is_first_run", false).apply()
            Log.d("DEBUG", "First run - preferences cleared")
        } else {
            Log.d("DEBUG", "Not first run - preferences preserved")
        }

        setContent {
            Practica4Theme {
                // Инициализация состояний с remember для сохранения при рекомпозиции

                // Список дней для прогноза погоды
                daysList = remember {
                    mutableStateOf(listOf<WeatherModel>())
                }

                // Состояние видимости диалога поиска
                val dialogState = remember {
                    mutableStateOf(false)
                }

                // История поиска городов
                val searchHistory = remember {
                    mutableStateListOf<String>().apply {
                        addAll(loadSearchHistory())
                    }
                }

                // Список избранных городов
                val favoriteCities = remember {
                    mutableStateListOf<FavoriteCity>().apply {
                        addAll(loadFavoriteCities())
                    }
                }

                // Данные текущего дня
                currentDay = remember {
                    mutableStateOf(WeatherModel(
                        "", "", "0.0", "", "", "", "0.0", "0.0",
                        "", "", "", "", ""
                    ))
                }

                val context = LocalContext.current

                // Настройки приложения
                val tempUnit = remember { mutableStateOf("C") }
                val windUnit = remember { mutableStateOf("kph") }
                val language = remember { mutableStateOf("RU") }

                // Загрузка сохраненных настроек
                val (savedTemp, savedWind, saveLang) = loadSettings(context)
                tempUnit.value = savedTemp
                windUnit.value = savedWind
                language.value = saveLang

                // Launcher для запроса разрешений на геолокацию
                val locationPermissionRequest = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

                    shouldRequestLocation.value = granted
                    if (!granted) {
                        Toast.makeText(this, "Доступ к геолокации запрещён", Toast.LENGTH_SHORT).show()
                    }
                }

                // Эффект для первоначальной загрузки данных
                LaunchedEffect(Unit) {
                    val lastLocation = loadLastLocation()
                    val permissionGranted = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                    shouldRequestLocation.value = permissionGranted

                    if (lastLocation != null) {
                        // Используем сохраненную локацию
                        val city = getCityName(Location("").apply {
                            latitude = lastLocation.first
                            longitude = lastLocation.second
                        })
                        getData(city, context, daysList, currentDay, tempUnit.value, windUnit.value)
                        updateFavoriteCitiesWeather(favoriteCities, city, context, tempUnit.value, language)
                    } else if (permissionGranted) {
                        // Разрешения есть - получаем текущую локацию
                        getLocation(daysList, currentDay, tempUnit.value, windUnit.value)
                    } else {
                        // Запрашиваем разрешения
                        locationPermissionRequest.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                }

                // Эффект для получения локации когда разрешения предоставлены
                LaunchedEffect(shouldRequestLocation.value) {
                    if (shouldRequestLocation.value) {
                        kotlinx.coroutines.delay(1000) // Пауза для инициализации GPS
                        getLocation(daysList, currentDay, tempUnit.value, windUnit.value)
                    }
                }

                // Отображение диалога поиска
                if (dialogState.value) {
                    DialogSearch(
                        dialogState = dialogState,
                        searchHistory = searchHistory,
                        onSubmit = { city ->
                            addToSearchHistory(city, searchHistory)
                            getData(city, context, daysList, currentDay, tempUnit.value, windUnit.value)
                        },
                        onClearHistory = {
                            clearSearchHistory(searchHistory)
                        },
                        language = language.value
                    )
                }

                // Launcher для экрана настроек
                val settingsLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        val data = result.data
                        val newTemp = data?.getStringExtra("TEMP_UNIT") ?: "C"
                        val newWind = data?.getStringExtra("WIND_UNIT") ?: "kph"
                        val newLang = data?.getStringExtra("LANGUAGE") ?: "RU"

                        // Обновление настроек и перезагрузка данных
                        tempUnit.value = newTemp
                        windUnit.value = newWind
                        language.value = newLang
                        saveSettings(context, newTemp, newWind, newLang)

                        getData(currentDay.value.city, context, daysList, currentDay, newTemp, newWind)
                        updateFavoriteCitiesWeather(favoriteCities, currentDay.value.city, context, newTemp, language)
                    }
                }

                // Launcher для экрана избранных городов
                val favoritesLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        val selectedCity = result.data?.getStringExtra("selected_city")
                        selectedCity?.let { city ->
                            getData(city, context, daysList, currentDay, tempUnit.value, windUnit.value)
                        }
                    }
                }

                // Основной UI приложения
                Box(modifier = Modifier.fillMaxSize()) {
                    Column (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(WindowInsets.systemBars.asPaddingValues()) // Учет системных баров
                    ) {
                        // Главная карточка с текущей погодой
                        MianCard(currentDay,
                            language,
                            windUnit,
                            tempUnit,
                            onClickSearch = {
                                dialogState.value = true // Открытие диалога поиска
                            },
                            onClickLocation = {
                                if (shouldRequestLocation.value) {
                                    Toast.makeText(context, "Определяю локацию...", Toast.LENGTH_SHORT).show()
                                    getLocation(daysList, currentDay, tempUnit.value, windUnit.value)
                                } else {
                                    Toast.makeText(context, "Доступ к геолокации запрещён", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onClickFavorites = {
                                // Открытие экрана избранных городов
                                val intent = FavoritesActivity.createIntent(
                                    context = this@MainActivity,
                                    currentCity = currentDay.value.city,
                                    language = language.value
                                )
                                favoritesLauncher.launch(intent)
                            },
                            onClickSettings = {
                                // Открытие экрана настроек
                                val intent = Intent(context, SettingsActivity::class.java).apply {
                                    putExtra("TEMP_UNIT", tempUnit.value)
                                    putExtra("WIND_UNIT", windUnit.value)
                                    putExtra("LANGUAGE", language.value)
                                }
                                settingsLauncher.launch(intent)
                            })
                        // Вкладки с почасовым прогнозом и прогнозом на 3 дня
                        TabLayout(daysList, currentDay, language, tempUnit.value)
                    }
                }
            }
        }
    }

    // Обработка результата из Activity избранных
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FAVORITES_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val selectedCity = data?.getStringExtra("selected_city")
            selectedCity?.let { city ->
                getData(city, this, daysList, currentDay, "C", "kph")
            }
        }
    }

    // ========== НАСТРОЙКИ ==========

    private fun saveSettings(context: Context, temp: String, wind: String, language: String) {
        val prefs = context.getSharedPreferences("settings", MODE_PRIVATE)
        prefs.edit()
            .putString("tempUnit", temp)
            .putString("windUnit", wind)
            .putString("language", language)
            .apply()
    }

    private fun loadSettings(context: Context): Triple<String, String, String> {
        val prefs = context.getSharedPreferences("settings", MODE_PRIVATE)
        val temp = prefs.getString("tempUnit", "C") ?: "C"
        val wind = prefs.getString("windUnit", "kph") ?: "kph"
        val lang = prefs.getString("language", "RU") ?: "RU"
        return Triple(temp, wind, lang)
    }



    // Обновление погоды для всех избранных городов
    private fun updateFavoriteCitiesWeather(
        favorites: MutableList<FavoriteCity>,
        currentCity: String,
        context: Context,
        tempUnit: String,
        language: MutableState<String>
    ) {
        if (favorites.isEmpty() || currentCity.isEmpty()) return

        favorites.forEach { favoriteCity ->
            updateSingleFavoriteWeather(favoriteCity.name, favorites, context, tempUnit, language)
        }
    }

    // Обновление погоды для одного избранного города
    private fun updateSingleFavoriteWeather(cityName: String, favorites: MutableList<FavoriteCity>,
                                            context: Context, tempUnit: String, language: MutableState<String>) {

        val url = "https://api.weatherapi.com/v1/forecast.json?key=$API_KEY&q=$cityName&days=1&aqi=no"
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(
            com.android.volley.Request.Method.GET,
            url,
            { response ->
                try {
                    val mainObject = JSONObject(response)
                    val current = mainObject.getJSONObject("current")

                    val forecast = mainObject.getJSONObject("forecast")
                    val forecastDay = forecast.getJSONArray("forecastday").getJSONObject(0)
                    val dayCondition = forecastDay.getJSONObject("day").getJSONObject("condition")
                    val translateDayCondition = if(language.value=="RU") translateText(dayCondition.getString("text"), "RU")
                    else dayCondition.getString("text")

                    val currentTemp = if (tempUnit == "F") current.getString("temp_f") else current.getString("temp_c")
                    val currentTempNow = currentTemp.toFloatOrNull()?.toInt()?.toString() + "°" ?: "0°"

                    val updatedCity = FavoriteCity(
                        name = cityName,
                        currentTemp = currentTempNow,
                        condition = translateDayCondition,
                        icon = dayCondition.getString("icon")
                    )

                    // Обновление в списке
                    val index = favorites.indexOfFirst { it.name == cityName }
                    if (index != -1) {
                        favorites[index] = updatedCity
                        saveFavoriteCities(favorites)
                    }
                } catch (e: Exception) {
                    Log.d("MyLog", "Error parsing forecast data: $e")
                }
            },
            { error ->
                Log.d("MyLog", "Error updating favorite city with forecast: $error")
            }
        )
        queue.add(request)
    }

    private fun saveFavoriteCities(favorites: List<FavoriteCity>) {
        val prefs = getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
        val jsonArray = JSONArray()
        favorites.forEach { city ->
            val jsonObject = JSONObject()
            jsonObject.put("name", city.name)
            jsonObject.put("currentTemp", city.currentTemp)
            jsonObject.put("condition", city.condition)
            jsonObject.put("icon", city.icon)
            jsonArray.put(jsonObject)
        }
        prefs.edit().putString(favoritesKey, jsonArray.toString()).apply()
    }

    private fun loadFavoriteCities(): List<FavoriteCity> {
        val prefs = getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
        val jsonString = prefs.getString(favoritesKey, null) ?: return emptyList()

        return try {
            val jsonArray = JSONArray(jsonString)
            val cities = mutableListOf<FavoriteCity>()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                cities.add(
                    FavoriteCity(
                        name = jsonObject.getString("name"),
                        currentTemp = jsonObject.getString("currentTemp"),
                        condition = jsonObject.getString("condition"),
                        icon = jsonObject.getString("icon")
                    )
                )
            }
            cities
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ========== ИСТОРИЯ ПОИСКА ==========

    private fun addToSearchHistory(city: String, history: MutableList<String>) {
        if (city.isBlank()) return
        // Удаляем если уже существует (чтобы переместить в начало)
        history.remove(city)
        history.add(0, city)
        // Ограничение размера истории
        if (history.size > 5) {
            history.removeAt(history.size - 1)
        }
        saveSearchHistory(history)
    }

    private fun clearSearchHistory(history: MutableList<String>) {
        history.clear()
        saveSearchHistory(history)
    }

    private fun saveSearchHistory(history: List<String>) {
        val prefs = getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
        prefs.edit().putStringSet(searchHistoryKey, history.toSet()).apply()
    }

    private fun loadSearchHistory(): List<String> {
        val prefs = getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
        return prefs.getStringSet(searchHistoryKey, setOf())?.toList() ?: emptyList()
    }

    // ========== ГЕОЛОКАЦИЯ ==========

    @SuppressLint("MissingPermission")
    private fun getLocation(
        daysList: MutableState<List<WeatherModel>>,
        currentDay: MutableState<WeatherModel>,
        tempUnit: String = "C",
        windUnit: String = "kph"
    ) {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000L
        ).setMaxUpdates(1).build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    if (location != null) {
                        val city = getCityName(location)
                        Log.d("GPS", "Город: $city (${location.latitude}, ${location.longitude})")
                        saveLastLocation(location.latitude, location.longitude)
                        getData(city, this@MainActivity, daysList, currentDay, tempUnit, windUnit)
                    } else {
                        Toast.makeText(this@MainActivity, "Не удалось определить локацию", Toast.LENGTH_SHORT).show()
                    }
                    fusedLocationClient.removeLocationUpdates(this)
                }
            },
            Looper.getMainLooper()
        )
    }

    private fun saveLastLocation(lat: Double, lon: Double) {
        val prefs = getSharedPreferences("weather_prefs", MODE_PRIVATE)
        prefs.edit()
            .putString("last_lat", lat.toString())
            .putString("last_lon", lon.toString())
            .apply()
    }

    private fun loadLastLocation(): Pair<Double, Double>? {
        val prefs = getSharedPreferences("weather_prefs", MODE_PRIVATE)
        val lat = prefs.getString("last_lat", null)?.toDoubleOrNull()
        val lon = prefs.getString("last_lon", null)?.toDoubleOrNull()
        return if (lat != null && lon != null) Pair(lat, lon) else null
    }

    private fun getCityName(location: Location): String {
        return try {
            val geocoder = Geocoder(this, Locale.ENGLISH)
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                listOfNotNull(
                    addresses[0].locality,
                    addresses[0].subAdminArea,
                    addresses[0].adminArea
                ).firstOrNull()?.trim() ?: "Unknown"
            } else {
                "Unknown"
            }
        } catch (e: Exception) {
            Log.e("GPS", "Ошибка при получении города: ${e.message}")
            "Unknown"
        }
    }
}

// ========== API ЗАПРОСЫ ==========

// Получение данных о погоде для города
private fun getData(
    city: String,
    context: Context,
    daysList: MutableState<List<WeatherModel>>,
    currentDay: MutableState<WeatherModel>,
    tempUnit: String = "C",
    windUnit: String = "kph",
    language: String = "EN"
) {
    if (city.isNullOrBlank()) {
        Log.d("MyLog", "Пустой город — не запрашиваю данные")
        return
    }

    val langParam = if(language == "RU") "ru" else "en"
    val url = "https://api.weatherapi.com/v1/forecast.json?key=$API_KEY&q=$city&days=7&aqi=no&alerts=no&lang=$langParam"

    val queue = Volley.newRequestQueue(context)
    val sRequest = StringRequest(
        com.android.volley.Request.Method.GET,
        url,
        { response ->
            val list = getWeatherByDays(response, tempUnit, windUnit)
            currentDay.value = list[0]
            daysList.value = list
        },
        { error ->
            Log.d("Mylog", "VolleyError: $error")
        }
    )
    queue.add(sRequest)
}

// Парсинг данных о погоде по дням
private fun getWeatherByDays(response: String, tempUnit: String, windUnit: String): List<WeatherModel> {
    if (response.isEmpty()) return listOf()
    val list = ArrayList<WeatherModel>()
    val mainObject = JSONObject(response)
    val city = mainObject.getJSONObject("location").getString("name")
    val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")

    for (i in 0 until days.length()) {
        val item = days[i] as JSONObject
        val dayObj = item.getJSONObject("day")

        // Выбор единиц измерения температуры
        val maxTemp = if (tempUnit == "F") dayObj.getString("maxtemp_f") else dayObj.getString("maxtemp_c")
        val minTemp = if (tempUnit == "F") dayObj.getString("mintemp_f") else dayObj.getString("mintemp_c")

        // Выбор единиц измерения ветра
        val maxWind = when (windUnit) {
            "mph" -> dayObj.getString("maxwind_mph")
            "kph" -> dayObj.getString("maxwind_kph")
            else -> dayObj.getString("maxwind_kph")
        }

        list.add(
            WeatherModel(
                city = city,
                time = item.getString("date"),
                currentTemp = "",
                condition = dayObj.getJSONObject("condition").getString("text"),
                icon = dayObj.getJSONObject("condition").getString("icon"),
                humidity = dayObj.getString("avghumidity"),
                maxTemp = maxTemp,
                minTemp = minTemp,
                hours = item.getJSONArray("hour").toString(),
                wind = maxWind,
                pressure = "",
                sunrise = item.getJSONObject("astro").getString("sunrise"),
                sunset = item.getJSONObject("astro").getString("sunset")
            )
        )
    }

    // Обновление текущей температуры для первого дня
    val currentObj = mainObject.getJSONObject("current")
    val currentTemp = if (tempUnit == "F") currentObj.getString("temp_f") else currentObj.getString("temp_c")

    list[0] = list[0].copy(
        time = currentObj.getString("last_updated"),
        currentTemp = currentTemp.toFloat().toInt().toString()
    )

    return list
}

// Получение подсказок для автодополнения городов
fun getCitySuggestions(
    query: String,
    context: Context,
    onResult: (List<String>) -> Unit
) {
    if (query.length < 2) {
        onResult(emptyList())
        return
    }

    val url = "https://api.weatherapi.com/v1/search.json?key=$API_KEY&q=$query"
    val queue = Volley.newRequestQueue(context)

    val request = StringRequest(
        com.android.volley.Request.Method.GET,
        url,
        { response ->
            try {
                val jsonArray = JSONArray(response)
                val cityList = mutableListOf<String>()

                for (i in 0 until jsonArray.length()) {
                    val cityObj = jsonArray.getJSONObject(i)
                    val name = cityObj.getString("name")
                    val country = cityObj.getString("country")
                    cityList.add("$name, $country")
                }

                onResult(cityList)
            } catch (e: Exception) {
                Log.e("MyLog", "Error parsing suggestions: $e")
                onResult(emptyList())
            }
        },
        { error ->
            Log.e("MyLog", "Error fetching suggestions: $error")
            onResult(emptyList())
        }
    )

    queue.add(request)
}