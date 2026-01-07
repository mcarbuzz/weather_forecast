package com.example.practica4.screens

import android.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.practica4.getCitySuggestions
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogSearch(
    dialogState: MutableState<Boolean>, // Состояние видимости диалога
    searchHistory: List<String>, // История предыдущих поисков
    onSubmit: (String) -> Unit, // Колбэк при выборе города
    onClearHistory: () -> Unit, // Колбэк очистки истории
    language: String // Язык интерфейса
) {
    // Локальные состояния диалога
    val dialogText = remember { mutableStateOf("") } // Текст в поле ввода
    val suggestions = remember { mutableStateOf<List<String>>(emptyList()) } // Список предложений
    val isLoading = remember { mutableStateOf(false) } // Состояние загрузки
    val context = LocalContext.current // Контекст для API вызовов

    // Эффект для поиска городов при изменении текста
    LaunchedEffect(dialogText.value) {
        if (dialogText.value.length >= 2) { // Поиск только при 2+ символах
            isLoading.value = true
            delay(500) // Задержка для дебаунса (чтобы не искать на каждый символ)
            getCitySuggestions(dialogText.value, context) { cityList ->
                suggestions.value = cityList
                isLoading.value = false
            }
        } else {
            suggestions.value = emptyList() // Очищаем предложения если текста мало
            isLoading.value = false
        }
    }

    // Диалоговое окно Material Design 3
    AlertDialog(
        onDismissRequest = { dialogState.value = false }, // Закрытие по клику вне диалога
        confirmButton = {
        },
        dismissButton = {
            // Нижняя панель кнопок
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Кнопка очистки истории (только если история не пустая)
                if (searchHistory.isNotEmpty()) {
                    TextButton(onClick = { onClearHistory() }) {
                        Text(
                            text = if (language == "RU") "Очистить историю" else "Clear History",
                            fontSize = 14.sp,
                            color = Color.Red
                        )
                    }
                }
                // Кнопка отмены
                TextButton(onClick = { dialogState.value = false }) {
                    Text(
                        text = if (language == "RU") "Отмена" else "Cancel",
                        fontSize = 16.sp
                    )
                }
            }
        },
        title = {
            // Заголовок диалога с полем ввода
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (language == "RU") "Введите название города:" else "Enter city name:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                // Поле ввода текста
                TextField(
                    value = dialogText.value,
                    onValueChange = { dialogText.value = it }, // Обновление текста
                    placeholder = {
                        Text(
                            text = if (language == "RU") "Например: Москва" else "For example: Moscow"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        // Индикатор загрузки в поле ввода
                        if (isLoading.value) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    },
                    singleLine = true // Однострочное поле
                )
            }
        },
        text = {
            // Основное содержимое диалога
            Column(modifier = Modifier.fillMaxWidth()) {

                // --- СЕКЦИЯ ПРЕДЛОЖЕНИЙ ---
                // Показываем найденные города если есть результаты
                if (suggestions.value.isNotEmpty()) {
                    Text(
                        text = if (language == "RU") "Найденные города:" else "Found cities:",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    // Список карточек с предложениями
                    suggestions.value.forEach { suggestion ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    // Извлекаем только название города (до запятой)
                                    val cityName = suggestion.substringBefore(",").trim()
                                    dialogState.value = false // Закрываем диалог
                                    onSubmit(cityName) // Передаем выбранный город
                                },
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)) // Белый фон
                        ) {
                            Text(
                                text = suggestion, // Полное название (город, регион, страна)
                                modifier = Modifier.padding(12.dp),
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                // --- СЕКЦИЯ ИСТОРИИ ПОИСКА ---
                // Показываем историю только если поле поиска пустое
                if (searchHistory.isNotEmpty() && dialogText.value.isEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (language == "RU") "История поиска:" else "Search history:",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    // Показываем только последние 5 записей
                    searchHistory.take(5).forEach { historyItem ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    dialogState.value = false
                                    onSubmit(historyItem) // Выбор из истории
                                },
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEFEF)) // Серый фон для истории
                        ) {
                            Text(
                                text = historyItem,
                                modifier = Modifier.padding(12.dp),
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                // --- СЕКЦИЯ СТАТУСА ЗАГРУЗКИ ---
                if (isLoading.value) {
                    // Индикатор поиска
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (language == "RU") "Поиск городов..." else "Searching cities...",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                } else if (suggestions.value.isEmpty() && dialogText.value.length >= 2) {
                    // Сообщение "не найдено" только после ввода 2+ символов
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (language == "RU") "Города не найдены. Попробуйте другой запрос."
                        else "Cities not found. Try another query.",
                        fontSize = 14.sp,
                        color = Color.Red // Красный цвет для ошибки
                    )
                }
            }
        }
    )
}