package com.example.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val NavyPrimary = Color(0xFF0F3A66)
val NavyOnPrimary = Color(0xFFFFFFFF)
val NavyContainer = Color(0xFFD6E4FF)
val NavyOnContainer = Color(0xFF001C3B)

val GreenSuccess = Color(0xFF2E7D32)
val GreenSuccessContainer = Color(0xFFE8F5E9)
val RedError = Color(0xFFC62828)
val RedErrorContainer = Color(0xFFFFEBEE)
val AmberAccent = Color(0xFFE65100)
val AmberContainer = Color(0xFFFFF3E0)

val SurfaceLight = Color(0xFFF8F9FC)
val SurfaceDark = Color(0xFF111418)

val LightColors = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = NavyOnPrimary,
    primaryContainer = NavyContainer,
    onPrimaryContainer = NavyOnContainer,
    secondary = Color(0xFF006782),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFBAEAFF),
    onSecondaryContainer = Color(0xFF001F29),
    tertiary = AmberAccent,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = AmberContainer,
    onTertiaryContainer = Color(0xFF321200),
    background = Color(0xFFFBFBFE),
    onBackground = Color(0xFF191C1E),
    surface = SurfaceLight,
    onSurface = Color(0xFF191C1E),
    surfaceVariant = Color(0xFFE0E2EC),
    onSurfaceVariant = Color(0xFF43474E),
    outline = Color(0xFF73777F)
)

val DarkColors = darkColorScheme(
    primary = Color(0xFFA6C8FF),
    onPrimary = Color(0xFF003060),
    primaryContainer = Color(0xFF004786),
    onPrimaryContainer = Color(0xFFD6E4FF),
    secondary = Color(0xFF62D3FF),
    onSecondary = Color(0xFF003544),
    secondaryContainer = Color(0xFF004D62),
    onSecondaryContainer = Color(0xFFBAEAFF),
    tertiary = Color(0xFFFFB68C),
    onTertiary = Color(0xFF522300),
    tertiaryContainer = Color(0xFF753400),
    onTertiaryContainer = Color(0xFFFFDBC9),
    background = Color(0xFF111418),
    onBackground = Color(0xFFE1E2E8),
    surface = Color(0xFF191C20),
    onSurface = Color(0xFFE1E2E8),
    surfaceVariant = Color(0xFF43474E),
    onSurfaceVariant = Color(0xFFC4C6D0),
    outline = Color(0xFF8D9199)
)
