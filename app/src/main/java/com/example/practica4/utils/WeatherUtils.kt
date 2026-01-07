package com.example.practica4.utils

import android.content.Context
import android.util.Log
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.practica4.data.FavoriteCity
import com.example.practica4.data.WeatherModel
import org.json.JSONArray
import org.json.JSONObject

import java.util.Locale

fun translateText(text: String, targetLang: String): String {
    val dictionary = mapOf(
        // 🌦 Погода
        "Sunny" to "Солнечно",
        "Clear" to "Ясно",
        "Partly Cloudy" to "Переменная облачность",
        "Cloudy" to "Облачно",
        "Overcast" to "Пасмурно",
        "Mist" to "Туман",
        "Patchy rain possible" to "Возможен небольшой дождь",
        "Patchy snow possible" to "Возможен небольшой снег",
        "Patchy sleet possible" to "Возможен небольшой мокрый снег",
        "Patchy rain nearby" to "Местами дождь",
        "Patchy freezing drizzle possible" to "Возможна небольшая моросящая изморось",
        "Thundery outbreaks possible" to "Возможны грозовые разряды",
        "Blowing snow" to "Снегопад с порывами ветра",
        "Blizzard" to "Метель",
        "Fog" to "Туман",
        "Freezing fog" to "Ледяной туман",
        "Patchy light drizzle" to "Небольшой моросящий дождь",
        "Light drizzle" to "Слабый дождь",
        "Heavy drizzle" to "Сильный моросящий дождь",
        "Patchy light rain" to "Возможен небольшой дождь",
        "Light rain" to "Небольшой дождь",
        "Moderate rain at times" to "Местами умеренный дождь",
        "Moderate rain" to "Умеренный дождь",
        "Heavy rain at times" to "Местами сильный дождь",
        "Heavy rain" to "Сильный дождь",
        "Light freezing rain" to "Слабый ледяной дождь",
        "Moderate or heavy freezing rain" to "Умеренный или сильный ледяной дождь",
        "Light sleet" to "Небольшой мокрый снег",
        "Moderate or heavy sleet" to "Умеренный или сильный мокрый снег",
        "Patchy light snow" to "Небольшой снег",
        "Light snow" to "Небольшой снег",
        "Patchy moderate snow" to "Местами умеренный снег",
        "Moderate snow" to "Умеренный снег",
        "Patchy heavy snow" to "Местами сильный снег",
        "Heavy snow" to "Сильный снег",
        "Ice pellets" to "Град",
        "Light rain shower" to "Небольшой дождь",
        "Moderate or heavy rain shower" to "Умеренный или сильный дождь",
        "Torrential rain shower" to "Ливень",
        "Light sleet showers" to "Небольшие ливни мокрого снега",
        "Moderate or heavy sleet showers" to "Умеренные или сильные ливни мокрого снега",
        "Light snow showers" to "Небольшой снег",
        "Moderate or heavy snow showers" to "Умеренный или сильный снег",
        "Light showers of ice pellets" to "Небольшой  град",
        "Moderate or heavy showers of ice pellets" to "Умеренный или сильный град",
        "Patchy light rain with thunder" to "Небольшой дождь с грозой",
        "Moderate or heavy rain with thunder" to "Умеренный или сильный дождь с грозой",
        "Patchy light snow with thunder" to "Небольшой снег с грозой",
        "Moderate or heavy snow with thunder" to "Умеренный или сильный снег с грозой",
    )

    return when (targetLang.uppercase(Locale.ROOT)) {
        "RU" -> dictionary[text.trim()] ?: text
        "EN" -> dictionary.entries.find { it.value == text.trim() }?.key ?: text
        else -> text
    }
}