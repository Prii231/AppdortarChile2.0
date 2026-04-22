package com.example.appdortarchile20.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val azulInstitucional = Color(0xFF003399)
    val azulClaro = Color(0xFF0055CC)
    val celesteLogo = Color(0xFF99CCFF)
    val rojoChile = Color(0xFFEF5350)

    // Estado para controlar cuándo inicia la animación
    var startAnimation by remember { mutableStateOf(false) }

    // Animación de escala: pasa de 0.8f a 1.2f suavemente
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1.1f else 0.8f,
        animationSpec = tween(durationMillis = 1000), // Dura 1 segundo
        label = "LogoScale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true // Inicia la animación al entrar
        delay(2500) // Tiempo total de espera
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(azulInstitucional, azulClaro)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.scale(scale) // Aplicamos la escala a toda la columna
        ) {
            // Logo principal
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "App",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "Doptar",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    color = celesteLogo
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Chile",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    color = rojoChile
                )
            }

            Text(
                text = "Encuentra tu alma gemela <3",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        // El cargador se queda fijo abajo, fuera de la animación de escala
        CircularProgressIndicator(
            color = Color.White.copy(alpha = 0.4f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp)
                .size(30.dp)
        )
    }
}