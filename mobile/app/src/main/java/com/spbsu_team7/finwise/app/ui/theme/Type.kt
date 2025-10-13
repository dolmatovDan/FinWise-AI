package com.spbsu_team7.finwise.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val FinanceTypography = Typography(
    displayLarge = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.sp), // для отображение статуса
    headlineMedium = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
    titleLarge = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold), // для названия приложения
    titleMedium = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold), // для наименований разделов
    bodyLarge = TextStyle(fontSize = 16.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.W500), // Название операций
    bodySmall = TextStyle(fontSize = 16.sp, color = lightText), // Примеры в полях
    labelLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
    labelSmall = TextStyle(fontSize = 11.sp, color = lightText) // Используется для маленького текста в операциях,

)
