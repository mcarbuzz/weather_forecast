package com.example.practica4

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.practica4.screens.SettingsScreenCards
import com.example.practica4.ui.theme.Practica4Theme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// окно настроек
class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentTemp = intent.getStringExtra("TEMP_UNIT") ?: "C"
        val currentWind = intent.getStringExtra("WIND_UNIT") ?: "kph"
        val currentLang = intent.getStringExtra("LANGUAGE") ?: "RU"

        setContent {
            SettingsScreenCards(
                currentTempUnit = currentTemp,
                currentWindUnit = currentWind,
                currentLanguage = currentLang,
                onSave = { temp, wind, lang ->
                    val resultIntent = Intent().apply {
                        putExtra("TEMP_UNIT", temp)
                        putExtra("WIND_UNIT", wind)
                        putExtra("LANGUAGE", lang)
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()
                },
                onBack = { finish() }
            )

        }
    }
}

