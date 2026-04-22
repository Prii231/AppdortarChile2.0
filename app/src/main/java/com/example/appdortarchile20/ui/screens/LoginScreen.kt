package com.example.appdortarchile20.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appdortarchile20.ui.viewmodel.PetViewModel
import com.example.appdortarchile20.ui.viewmodel.LoginState

@Composable
fun LoginScreen(
    viewModel: PetViewModel,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginState by viewModel.loginState.collectAsState()

    // Colores institucionales
    val azulInstitucional = Color(0xFF003399)
    val azulClaro = Color(0xFF0055CC)
    val celesteLogo = Color(0xFF99CCFF)
    val rojoChile = Color(0xFFEF5350)

    // --- ANIMACIÓN DEL CORAZÓN ---
    val infiniteTransition = rememberInfiniteTransition(label = "HeartBeat")
    val heartScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HeartScale"
    )

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            onLoginSuccess()
        }
    }

    // Contenedor principal con Degradado
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(azulInstitucional, azulClaro)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // --- LOGO TRICOLOR ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("App", fontSize = 38.sp, fontWeight = FontWeight.Black, color = Color.White)
                Text("Doptar", fontSize = 38.sp, fontWeight = FontWeight.Black, color = celesteLogo)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Chile", fontSize = 38.sp, fontWeight = FontWeight.Black, color = rojoChile)
            }

            // --- ESCLOGAN CON CORAZÓN QUE PALPITA ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Encuentra tu alma gemela ",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 16.sp
                )
                Text(
                    text = "<3",
                    color = rojoChile,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.scale(heartScale) // Aquí se aplica el latido
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // --- FORMULARIO (Caja Blanca Semi-transparente para legibilidad) ---
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo Electrónico", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                            focusedBorderColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña", color = Color.White) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                            focusedBorderColor = Color.White
                        )
                    )
                }
            }

            if (loginState is LoginState.Error) {
                Text(
                    text = (loginState as LoginState.Error).message,
                    color = Color.Yellow, // Amarillo para que resalte sobre el azul
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- BOTONES ---
            Button(
                onClick = { viewModel.login(email, password) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Iniciar Sesión", color = azulInstitucional, fontWeight = FontWeight.Bold)
            }

            TextButton(onClick = onRegisterClick, modifier = Modifier.padding(top = 8.dp)) {
                Text("¿No tienes cuenta? Regístrate aquí", color = Color.White)
            }
        }
    }
}