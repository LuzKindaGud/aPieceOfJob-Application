package com.bsit.peerx.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.bsit.peerx.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val OrbitronFont = GoogleFont("Orbitron")
val OrbitronFamily = FontFamily(
    Font(googleFont = OrbitronFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = OrbitronFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = OrbitronFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = OrbitronFont, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = OrbitronFont, fontProvider = provider, weight = FontWeight.ExtraBold),
    Font(googleFont = OrbitronFont, fontProvider = provider, weight = FontWeight.Black),
)

// 3. Khai bÃ¡o font Rajdhani
val RajdhaniFont = GoogleFont("Rajdhani")
val RajdhaniFamily = FontFamily(
    Font(googleFont = RajdhaniFont, fontProvider = provider, weight = FontWeight.Light),
    Font(googleFont = RajdhaniFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = RajdhaniFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = RajdhaniFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = RajdhaniFont, fontProvider = provider, weight = FontWeight.Bold),
)

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = OrbitronFamily,
        fontWeight = FontWeight.Black,
        fontSize = 28.sp,
        letterSpacing = 3.sp,
        color = TextPrimary
    ),
    displayMedium = TextStyle(
        fontFamily = OrbitronFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        letterSpacing = 2.sp,
        color = TextPrimary
    ),
    headlineLarge = TextStyle(
        fontFamily = OrbitronFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        letterSpacing = 1.5.sp,
        color = TextPrimary
    ),
    headlineMedium = TextStyle(
        fontFamily = OrbitronFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        letterSpacing = 1.sp,
        color = TextPrimary
    ),
    titleLarge = TextStyle(
        fontFamily = RajdhaniFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        letterSpacing = 1.sp,
        color = TextPrimary
    ),
    titleMedium = TextStyle(
        fontFamily = RajdhaniFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        letterSpacing = 0.5.sp,
        color = TextSecondary
    ),
    bodyLarge = TextStyle(
        fontFamily = RajdhaniFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        color = TextPrimary
    ),
    bodyMedium = TextStyle(
        fontFamily = RajdhaniFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        color = TextPrimary
    ),
    labelLarge = TextStyle(
        fontFamily = RajdhaniFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        letterSpacing = 1.sp,
        color = TextPrimary
    ),
    labelSmall = TextStyle(
        fontFamily = RajdhaniFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 0.5.sp,
        color = TextMuted
    )
)



