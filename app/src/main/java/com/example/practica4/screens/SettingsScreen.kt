package com.example.practica4.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import com.example.practica4.ui.theme.BlueLight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenCards(
    currentTempUnit: String,
    currentWindUnit: String,
    currentLanguage: String,
    onSave: (String, String, String) -> Unit,
    onBack: () -> Unit
) {
    var tempUnit by remember { mutableStateOf(currentTempUnit) }
    var windUnit by remember { mutableStateOf(currentWindUnit) }
    var language by remember { mutableStateOf(currentLanguage) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (language == "RU") "Настройки" else "Settings",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
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
                .padding(16.dp)
        ) {

            // --- Температура ---
            Text(
                text = if (language == "RU") "Температура" else "Temperature",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                listOf("C", "F").forEach { unit ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .clickable { tempUnit = unit },
                        colors = CardDefaults.cardColors(
                            containerColor = if (tempUnit == unit) BlueLight else Color(0xFFEFEFEF)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (unit == "C") "°C" else "°F",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (tempUnit == unit) Color.White else Color.Black
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Скорость ветра ---
            Text(
                text = if (language == "RU") "Скорость ветра" else "Wind Speed",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                listOf("kph", "mph").forEach { unit ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .clickable { windUnit = unit },
                        colors = CardDefaults.cardColors(
                            containerColor = if (windUnit == unit) BlueLight else Color(0xFFEFEFEF)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = unit,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (windUnit == unit) Color.White else Color.Black
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Язык интерфейса ---
            Text(
                text = if (language == "RU") "Язык интерфейса" else "Interface Language",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                listOf("RU", "EN").forEach { lang ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .clickable { language = lang },
                        colors = CardDefaults.cardColors(
                            containerColor = if (language == lang) BlueLight else Color(0xFFEFEFEF)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (lang == "RU") "Русский" else "English",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (language == lang) Color.White else Color.Black
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // --- Кнопки ---
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

                Button(
                    onClick = { onSave(tempUnit, windUnit, language); onBack() },
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BlueLight)
                ) {
                    Text(
                        text = if (language == "RU") "Сохранить" else "Save",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
