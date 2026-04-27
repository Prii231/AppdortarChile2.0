package com.example.appdortarchile20.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// --- ESQUEMA CLARO ---
private val LightColorScheme = lightColorScheme(
    primary          = Naranja600,        // Botones, FAB, elementos activos
    onPrimary        = Color.White,
    primaryContainer = Naranja100,        // Chips, badges, fondos suaves
    onPrimaryContainer = Naranja800,

    secondary        = Verde600,          // Elementos secundarios
    onSecondary      = Color.White,
    secondaryContainer = Verde100,
    onSecondaryContainer = Verde700,

    tertiary         = Naranja500,        // Acentos y highlights
    onTertiary       = Color.White,
    tertiaryContainer = Naranja50,
    onTertiaryContainer = Naranja700,

    background       = CremaBg,           // Fondo general cálido
    onBackground     = OscuroCaldo,
    surface          = CremaCard,         // Tarjetas y superficies
    onSurface        = OscuroCaldo,
    surfaceVariant   = Naranja50,
    onSurfaceVariant = GrisCaldo,

    error            = RojoUrgencia,
    onError          = Color.White,

    outline          = Color(0xFFD4C0A8), // Bordes cálidos
    outlineVariant   = Color(0xFFEDE0CC),
)

// --- ESQUEMA OSCURO ---
private val DarkColorScheme = darkColorScheme(
    primary          = NaranjaClaro,
    onPrimary        = Color(0xFF4A2000),
    primaryContainer = Naranja800,
    onPrimaryContainer = Naranja100,

    secondary        = VerdeClaro,
    onSecondary      = Color(0xFF0D2B1A),
    secondaryContainer = Verde800,
    onSecondaryContainer = Verde100,

    tertiary         = Naranja500,
    onTertiary       = Color(0xFF3D1E00),

    background       = FondoOscuro,
    onBackground     = Color(0xFFF5E6D3),
    surface          = SuperfOscura,
    onSurface        = Color(0xFFF5E6D3),
    surfaceVariant   = Color(0xFF3D2910),
    onSurfaceVariant = Color(0xFFD4B896),

    error            = Color(0xFFFF8A80),
    onError          = Color(0xFF690000),

    outline          = Color(0xFF6B5040),
    outlineVariant   = Color(0xFF3D2910),
)

@Composable
fun AppdortarChile20Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Colorear la barra de estado con el naranja principal
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}