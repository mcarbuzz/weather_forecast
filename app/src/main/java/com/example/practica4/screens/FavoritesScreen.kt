package com.example.practica4.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.practica4.data.FavoriteCity
import com.example.practica4.ui.theme.BlueLight
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.ui.text.style.TextAlign

/**
 * Экран для управления избранными городами
 * Позволяет добавлять, удалять и выбирать города из избранного
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    currentCity: String, // Текущий город
    favoriteCities: List<FavoriteCity>, // Список избранных городов
    language: String, // Язык
    onBack: () -> Unit, // Колбэк для возврата на предыдущий экран
    onAddCurrentToFavorites: () -> Unit, // Колбэк для добавления текущего города в избранное
    onRemoveFromFavorites: (String) -> Unit, // Колбэк для удаления города из избранного по имени
    onSelectCity: (String) -> Unit // Колбэк для выбора города из списка избранных
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (language == "RU") "Избранные города" else "Favorite Cities",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    // Кнопка назад в верхнем левом углу
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = if (language == "RU") "Назад" else "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BlueLight,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF5F5F5))
        ) {
            // Баннер добавления текущего города в избранное
            // Показываем только если:
            // 1. Текущий город не пустой
            // 2. Текущий город еще не в избранном
            // 3. Не достигнут лимит избранных городов (10)
            if (currentCity.isNotBlank() &&
                favoriteCities.none { it.name == currentCity } &&
                favoriteCities.size < 10) {

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { onAddCurrentToFavorites() }, // Кликабельная карточка
                    colors = CardDefaults.cardColors(containerColor = BlueLight),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Иконка избранного (пустое сердечко)
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = if (language == "RU") "Добавить в избранное" else "Add to favorites",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        // Текстовая информация
                        Column(
                            modifier = Modifier
                                .weight(1f) // Занимает все доступное пространство
                                .padding(start = 12.dp)
                        ) {
                            Text(
                                text = if (language == "RU") "Добавить текущий город" else "Add current city",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = currentCity,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Список избранных городов
            if (favoriteCities.isEmpty()) {
                // Показываем состояние "пусто", если нет избранных городов
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (language == "RU") "Нет избранных городов" else "No favorite cities",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                // Показываем список избранных городов
                FavoritesList(
                    favoriteCities = favoriteCities,
                    onRemoveFromFavorites = onRemoveFromFavorites,
                    onSelectCity = onSelectCity,
                    language = language
                )
            }

            // Предупреждение о достижении лимита избранных городов
            if (favoriteCities.size >= 10) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)), // Светло-красный фон
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (language == "RU")
                            "Достигнут лимит избранных городов (10)"
                        else
                            "Favorite cities limit reached (10)",
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    )
                }
            }
        }
    }
}


/**
 * Список избранных городов с возможностью удаления и выбора
 */
@Composable
fun FavoritesList(
    favoriteCities: List<FavoriteCity>,
    onRemoveFromFavorites: (String) -> Unit,
    onSelectCity: (String) -> Unit,
    language: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Заголовок списка
        Text(
            text = if (language == "RU") "Избранные города" else "Favorite cities",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray,
            modifier = Modifier.padding(16.dp)
        )

        // LazyColumn для эффективного отображения списка
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Для каждого города создаем карточку
            items(favoriteCities) { city ->
                FavoriteCityCard(
                    city = city,
                    onRemove = { onRemoveFromFavorites(city.name) },
                    onSelect = { onSelectCity(city.name) }
                )
                Spacer(modifier = Modifier.height(8.dp)) // Отступ между карточками
            }
        }
    }
}

/**
 * Карточка отдельного избранного города
 * Отображает название города, текущую погоду и предоставляет действия
 */
@Composable
fun FavoriteCityCard(
    city: FavoriteCity, // Данные города
    onRemove: () -> Unit, // Колбэк удаления города
    onSelect: () -> Unit // Колбэк выбора города
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }, // Кликабельная для выбора города
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Распределение по ширине
        ) {
            // Левая часть - информация о городе
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f) // Занимает доступное пространство
            ) {
                // Иконка местоположения
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Город",
                    tint = BlueLight,
                    modifier = Modifier.size(24.dp)
                )

                // Текстовая информация о городе
                Column(
                    modifier = Modifier.padding(start = 12.dp)
                ) {
                    // Название города
                    Text(
                        text = city.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )

                    // Информация о погоде (если доступна)
                    if (city.currentTemp.isNotBlank() && city.condition.isNotBlank()) {
                        Text(
                            text = "${city.currentTemp} • ${city.condition}",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            // Правая часть - иконка погоды и кнопка удаления
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Иконка текущей погоды (загружается из интернета)
                if (city.icon.isNotBlank()) {
                    AsyncImage(
                        model = "https:${city.icon}",
                        contentDescription = "Погода",
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Кнопка удаления города из избранного
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить",
                        tint = Color.Red // Красный цвет для опасного действия
                    )
                }
            }
        }
    }
}