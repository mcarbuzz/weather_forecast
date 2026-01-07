package com.example.practica4.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.practica4.R
import com.example.practica4.ui.theme.BlueLight
import androidx.compose.ui.graphics.Color
import com.example.practica4.data.WeatherModel
import com.example.practica4.utils.translateText
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow


@Composable
fun MianCard(
    currentDay: MutableState<WeatherModel>, // Данные погоды
    language: MutableState<String>, // Язык
    windUnit: MutableState<String>, // kph/mph
    tempUnit: MutableState<String>, // C/F
    onClickSearch: () -> Unit, // Колбэк для поиска
    onClickLocation: () -> Unit, // Колбэк для локации
    onClickFavorites: () -> Unit, // Колбэк для избранного
    onClickSettings: () -> Unit // Колбэк для настроек
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        // Карточка с избран, поиском и геолокацией
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(50), // Скругленные углы
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Кнопка избранного
                WeatherActionButton(
                    icon = R.drawable.outline_bookmark_heart_24,
                    contentDescription = if (language.value == "RU") "Избранное" else "Favorites",
                    onClick = onClickFavorites
                )
                // Кнопка поиска
                WeatherActionButton(
                    icon = R.drawable.search,
                    contentDescription = if (language.value == "RU") "Поиск" else "Search",
                    onClick = onClickSearch
                )
                // Кнопка геолокации
                WeatherActionButton(
                    icon = R.drawable.gps,
                    contentDescription = if (language.value == "RU") "Локация" else "Location",
                    onClick = onClickLocation
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        // Главная карточка с температурой и описанием
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Тень
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Кнопка настроек
                IconButton(
                    onClick = onClickSettings,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.TopEnd)
                        .padding(top = 12.dp, end = 12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_settings_24),
                        contentDescription = if (language.value == "RU") "Настройки" else "Settings",
                        tint = BlueLight,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Основная карточка
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Название города
                    Text(
                        text = currentDay.value.city,
                        style = TextStyle(fontSize = 26.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                    )

                    Spacer(modifier = Modifier.height(1.dp))

                    // Текущая температура: удаляем °, преобразуем в int и обратно в string
                    val tempText = currentDay.value.currentTemp
                        .replace("°", "")
                        .toFloatOrNull()?.toInt()?.toString() ?: "${currentDay.value.maxTemp}°/${currentDay.value.minTemp}°"

                    Text(
                        text = tempText + "°",
                        style = TextStyle(fontSize = 80.sp, color = Color.Black, fontWeight = FontWeight.Light)
                    )

                    Spacer(modifier = Modifier.height(1.dp))

                    // Иконка погоды и описание
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Иконки из API
                        AsyncImage(
                            model = "https:${currentDay.value.icon}",
                            contentDescription = null,
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Перевод описания погоды
                        Text(
                            translateText(currentDay.value.condition, language.value),
                            style = TextStyle(fontSize = 18.sp, color = Color.Gray)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Карточка с: влажность, ветер, восход, закат
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                WeatherDetailItem(
                    icon = R.drawable.baseline_water_drop_24,
                    title = if (language.value == "RU") "Влажность" else "Humidity",
                    value = "${currentDay.value.humidity}%"
                )
                WeatherDetailItem(
                    icon = R.drawable.outline_air_24,
                    title = if (language.value == "RU") "Ветер" else "Wind",
                    value = getWindString(currentDay.value.wind, windUnit.value, language.value)
                )
                WeatherDetailItem(
                    icon = R.drawable.star,
                    title = if (language.value == "RU") "Восход" else "Sunrise",
                    value = currentDay.value.sunrise
                )
                WeatherDetailItem(
                    icon = R.drawable.outline_brightness_2_24,
                    title = if (language.value == "RU") "Закат" else "Sunset",
                    value = currentDay.value.sunset
                )
            }
        }
    }
}

/**
 * Компонент для отображения деталей погоды
 * @param icon Иконка параметра
 * @param title Название параметра
 * @param value Значение параметра
 */
@Composable
fun WeatherDetailItem(
    icon: Int,
    title: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Иконка параметра
        Icon(
            painter = painterResource(id = icon),
            contentDescription = title,
            tint = BlueLight,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        // Название параметра
        Text(text = title, style = TextStyle(fontSize = 13.sp, color = Color.Gray))
        // Значение параметра
        Text(text = value, style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium))
    }
}

/**
 * Кнопка действия с иконкой
 * @param icon Ресурс иконки
 * @param contentDescription Описание для accessibility
 * @param onClick Обработчик клика
 */
@Composable
fun WeatherActionButton(
    icon: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick, modifier = Modifier.size(48.dp)) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = contentDescription,
            tint = BlueLight,
            modifier = Modifier.size(26.dp)
        )
    }
}

/**
 * Форматирование строки ветра с учетом единиц измерения и языка
 * @param wind Скорость ветра
 * @param windUnit Единицы измерения (kph/mph)
 * @param language Язык интерфейса
 * @return Отформатированная строка ветра
 */
private fun getWindString(wind: String, windUnit: String, language: String): String {
    val windSpeed = wind.toFloatOrNull()?.toInt() ?: 0
    return when {
        language == "RU" && windUnit == "kph" -> "$windSpeed км/ч"
        language == "RU" && windUnit == "mph" -> "$windSpeed м/ч"
        language == "EN" && windUnit == "kph" -> "$windSpeed kph"
        language == "EN" && windUnit == "mph" -> "$windSpeed mph"
        else -> "$windSpeed"
    }
}


//Погода по дням и часам
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabLayout(
    daysList: MutableState<List<WeatherModel>>, // Список дней
    currentDay: MutableState<WeatherModel>, // Текущий день для почасового прогноза
    language: MutableState<String>, // Язык
    tempUnit: String = "C" // Единицы температуры
) {
    // Список заголовков вкладок в зависимости от языка
    val tabList = if (language.value == "RU") listOf("СЕГОДНЯ", "3 ДНЯ") else listOf("TODAY", "3 DAYS")
    // Состояние пагинатора для управления вкладками
    val pagerState = rememberPagerState(pageCount = { tabList.size })
    val coroutineScope = rememberCoroutineScope()
    val tabIndex = pagerState.currentPage

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // --- Вкладки ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(50),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            TabRow(
                selectedTabIndex = tabIndex,
                containerColor = Color.Transparent,
                contentColor = BlueLight,
                indicator = { pos ->
                    // Индикатор текущей вкладки
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(pos[tabIndex]),
                        color = BlueLight
                    )
                },
                divider = {}
            ) {
                tabList.forEachIndexed { index, text ->
                    Tab(
                        selected = tabIndex == index,
                        onClick = {
                            // Анимация переключения вкладок
                            coroutineScope.launch { pagerState.animateScrollToPage(index) }
                        },
                        text = {
                            Text(
                                text = text,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = if (tabIndex == index) FontWeight.Medium else FontWeight.Normal,
                                    color = if (tabIndex == index) BlueLight else Color.Gray
                                )
                            )
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        //Контент вкладок
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { index ->
            // Определяем какие данные показывать в зависимости от выбранной вкладки
            val list = when (index) {
                0 -> getWeatherByHours(currentDay.value.hours, tempUnit) // Почасовой прогноз на сегодня
                1 -> daysList.value // Прогноз на 3 дня
                else -> daysList.value
            }
            WeatherListCard(list, language)
        }
    }
}

//Список погоды

@Composable
fun WeatherListCard(
    list: List<WeatherModel>,
    language: MutableState<String>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        // LazyColumn для эффективного отображения списка
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
        ) {
            itemsIndexed(list) { i, item ->
                WeatherListItem(item, language)
                // Разделитель между элементами (кроме последнего)
                if (i < list.lastIndex)
                    Divider(
                        color = Color(0xFFE0E0E0),
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
            }
        }
    }
}

/**
 * Элемент списка погоды
 * Отображает время/день, иконку погоды, описание и температуру
 */
@Composable
fun WeatherListItem(
    item: WeatherModel,
    language: MutableState<String>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween // Распределение по ширине
    ) {
        // Левая часть — время и иконка погоды
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Время
            Text(
                text = if (item.time.isNotEmpty())
                    item.time.takeLast(5)
                else
                    item.city,
                style = TextStyle(
                    fontSize = 15.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = Modifier.width(10.dp))
            // Иконка погоды
            AsyncImage(
                model = "https:${item.icon}",
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
        }

        // описание погоды
        Text(
            translateText(item.condition, language.value),
            style = TextStyle(fontSize = 15.sp, color = Color.Gray),
            modifier = Modifier.weight(1f, fill = false), // Занимает доступное пространство
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis // Многоточие если текст не помещается
        )

        // температура
        Text(
            text = item.currentTemp.ifEmpty { "${item.maxTemp}°/${item.minTemp}°" },
            style = TextStyle(
                fontSize = 18.sp,
                color = BlueLight,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

// ===================== Получение данных по часам =====================

private fun getWeatherByHours(hours: String, tempUnit: String): List<WeatherModel> {
    if (hours.isEmpty()) return listOf()
    val hoursArray = JSONArray(hours)
    val list = ArrayList<WeatherModel>()

    // Парсинг каждого часа из JSON массива
    for (i in 0 until hoursArray.length()) {
        val item = hoursArray[i] as JSONObject
        // Выбор температуры в нужных единицах измерения
        val temp = if (tempUnit == "F")
            item.getString("temp_f")
        else
            item.getString("temp_c")

        list.add(
            WeatherModel(
                city = "",
                time = item.getString("time"),
                currentTemp = temp.toFloat().toInt().toString() + "°", // Округление температуры
                condition = item.getJSONObject("condition").getString("text"),
                icon = item.getJSONObject("condition").getString("icon"),
                humidity = item.getString("humidity"),
                maxTemp = "",
                minTemp = "",
                hours = "",
                wind = "",
                pressure = "",
                sunrise = "",
                sunset = ""
            )
        )
    }
    return list
}