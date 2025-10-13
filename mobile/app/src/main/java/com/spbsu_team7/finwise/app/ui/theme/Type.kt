package com.spbsu_team7.finwise.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val FinanceTypography = Typography(
    displayLarge = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.sp),
    headlineMedium = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
    titleLarge = TextStyle(fontSize = 25.sp, fontWeight = FontWeight.SemiBold), // для названия
    titleMedium = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold), // для наименований разделов
    bodyLarge = TextStyle(fontSize = 16.sp),
    bodyMedium = TextStyle(fontSize = 14.sp),
    labelLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
    labelSmall = TextStyle(fontSize = 10.sp, color = lightText) // Используется для маленького текста в названии,

)
