package com.example.practica4.data

data class WeatherModel(
    val city: String,
    val time: String,
    val currentTemp: String,
    val condition: String,
    val icon: String,
    val humidity: String,
    val maxTemp: String,
    val minTemp: String,
    val hours: String,
    val wind: String,
    val pressure: String,
    val sunrise: String,
    val sunset: String
)
