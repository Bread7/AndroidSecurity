package com.example.ict2215_project.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ict2215_project.R

// Set of Material typography styles to start with
val robotoFamily = FontFamily(
    Font(R.font.roboto_light, FontWeight.Light),
    Font(R.font.roboto_regular, FontWeight.Normal),
    Font(R.font.roboto_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.roboto_medium, FontWeight.Medium),
    Font(R.font.roboto_bold, FontWeight.Bold),
    Font(R.font.roboto_black, FontWeight.Black)
)

val mplusFamily = FontFamily(
    Font(R.font.m_plus_rounded1c_bold, FontWeight.Bold),
    Font(R.font.m_plus_rounded1c_black, FontWeight.Black),
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = robotoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 21.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.5.sp
    ),

    bodySmall = TextStyle(
        fontFamily = robotoFamily,
        fontWeight = FontWeight.Light,
        fontSize = 18.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.5.sp
    ),

    bodyMedium = TextStyle(
        fontFamily = robotoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 21.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.5.sp
    ),

    titleLarge = TextStyle(
        fontFamily = mplusFamily,
        fontWeight = FontWeight.Black,
        fontSize = 32.sp,
        lineHeight = 48.sp,
        letterSpacing = 0.sp
    ),

    titleSmall = TextStyle(
        fontFamily = robotoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),

    titleMedium = TextStyle(
        fontFamily = robotoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 21.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.5.sp
    )/* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)