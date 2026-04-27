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
    val naranjaPrincipal = Color(0xFFE85D04)
    val naranjaClaro     = Color(0xFFFB8500)
    val verdeSalvia      = Color(0xFF4A7C59)
    val crema            = Color(0xFFFFF5EB)

    var startAnimation by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1.1f else 0.8f,
        animationSpec = tween(durationMillis = 1000),
        label = "LogoScale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2500)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(naranjaPrincipal, naranjaClaro)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.scale(scale)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("App",    fontSize = 42.sp, fontWeight = FontWeight.Black, color = Color.White)
                Text("Doptar", fontSize = 42.sp, fontWeight = FontWeight.Black, color = crema)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Chile",  fontSize = 42.sp, fontWeight = FontWeight.Black, color = verdeSalvia)
            }
            Text(
                text = "Encuentra tu alma gemela <3",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        CircularProgressIndicator(
            color = Color.White.copy(alpha = 0.5f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp)
                .size(30.dp)
        )
    }
}