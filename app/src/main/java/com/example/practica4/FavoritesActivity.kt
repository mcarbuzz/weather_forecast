package com.example.practica4

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.example.practica4.data.FavoriteCity
import com.example.practica4.screens.FavoritesScreen
import com.example.practica4.ui.theme.Practica4Theme


class FavoritesActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Получаем данные переданные из MainActivity через Intent
        val currentCity = intent.getStringExtra("current_city") ?: ""
        val language = intent.getStringExtra("language") ?: "RU"

        setContent {
            Practica4Theme {
                // Создаем реактивный список избранных городов
                val favoriteCities = remember {
                    mutableStateListOf<FavoriteCity>().apply {
                        // Загружаем сохраненные города при инициализации
                        addAll(loadFavoriteCities())
                    }
                }

                // Отображаем экран избранных городов
                FavoritesScreen(
                    currentCity = currentCity,
                    favoriteCities = favoriteCities,
                    language = language,
                    onBack = { finish() },
                    onAddCurrentToFavorites = {
                        // Проверяем лимит перед добавлением
                        if (favoriteCities.size < 10) {
                            addCurrentToFavorites(currentCity, favoriteCities, language)
                        }
                    },
                    onRemoveFromFavorites = { cityName ->
                        removeFromFavorites(cityName, favoriteCities)
                    },
                    onSelectCity = { cityName ->
                        // Возвращаем выбранный город в MainActivity
                        setResult(RESULT_OK, Intent().apply {
                            putExtra("selected_city", cityName)
                        })
                        finish() // Закрываем Activity после выбора
                    }
                )
            }
        }
    }

//Добавляет текущий город в список избранных
    private fun addCurrentToFavorites(
        currentCity: String,
        favorites: MutableList<FavoriteCity>,
        language: String
    ) {

        // Создаем объект FavoriteCity с базовыми данными
        val favoriteCity = FavoriteCity(
            name = currentCity,
            currentTemp = "0°",
            condition = if (language == "RU") "Обновление..." else "Updating...",
            icon = ""
        )

        // Проверяем, что город еще не в избранном, затем добавляем в начало списка
        if (favorites.none { it.name == currentCity }) {
            favorites.add(0, favoriteCity) // Добавляем в начало
            saveFavoriteCities(favorites) // Сохраняем изменения
        }
    }

//Удаляет город из избранного по имени
    private fun removeFromFavorites(cityName: String, favorites: MutableList<FavoriteCity>) {
        // Удаляем все вхождения
        favorites.removeAll { it.name == cityName }
        saveFavoriteCities(favorites) // Сохраняем изменения
    }

//Сохраняет список избранных городов в SharedPreferences
    private fun saveFavoriteCities(favorites: List<FavoriteCity>) {
        val prefs = getSharedPreferences("weather_prefs", MODE_PRIVATE)
        val jsonArray = org.json.JSONArray()

        // Преобразуем каждый город в JSON объект
        favorites.forEach { city ->
            val jsonObject = org.json.JSONObject()
            jsonObject.put("name", city.name)
            jsonObject.put("currentTemp", city.currentTemp)
            jsonObject.put("condition", city.condition)
            jsonObject.put("icon", city.icon)
            jsonArray.put(jsonObject) // Добавляем в JSON массив
        }

        // Сохраняем JSON строку в SharedPreferences
        prefs.edit().putString("favorite_cities", jsonArray.toString()).apply()
    }

//Загружает список избранных городов из SharedPreferences

    private fun loadFavoriteCities(): List<FavoriteCity> {
        val prefs = getSharedPreferences("weather_prefs", MODE_PRIVATE)
        val jsonString = prefs.getString("favorite_cities", null) ?: return emptyList()

        return try {
            val jsonArray = org.json.JSONArray(jsonString)
            val cities = mutableListOf<FavoriteCity>()

            // Парсим каждый JSON объект в FavoriteCity
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
            // В случае ошибки парсинга возвращаем пустой список
            emptyList()
        }
    }

    /**
     * Компаньон объект для удобного создания Intent
     * Паттерн Factory Method для создания правильно настроенного Intent
     */
    companion object {
        /**
         * Создает Intent для запуска FavoritesActivity
         * @param context Контекст приложения
         * @param currentCity Текущий выбранный город
         * @param language Язык интерфейса
         * @return Intent с установленными параметрами
         */
        fun createIntent(context: android.content.Context, currentCity: String, language: String): Intent {
            return Intent(context, FavoritesActivity::class.java).apply {
                putExtra("current_city", currentCity)
                putExtra("language", language)
            }
        }
    }
}