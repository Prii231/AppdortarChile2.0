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

    // Paleta cálida
    val naranjaPrincipal = Color(0xFFE85D04)
    val naranjaClaro     = Color(0xFFFB8500)
    val verdeSalvia      = Color(0xFF4A7C59)
    val crema            = Color(0xFFFFF5EB)

    // Animación del corazón
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
        if (loginState is LoginState.Success) onLoginSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(naranjaPrincipal, naranjaClaro)
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
            // Logo tricolor cálido
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("App",    fontSize = 38.sp, fontWeight = FontWeight.Black, color = Color.White)
                Text("Doptar", fontSize = 38.sp, fontWeight = FontWeight.Black, color = crema)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Chile",  fontSize = 38.sp, fontWeight = FontWeight.Black, color = verdeSalvia)
            }

            // Eslogan con corazón
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Encuentra tu alma gemela ",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 16.sp
                )
                Text(
                    text = "<3",
                    color = crema,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.scale(heartScale)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Formulario
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
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
                            focusedBorderColor = Color.White,
                            cursorColor = Color.White
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
                            focusedBorderColor = Color.White,
                            cursorColor = Color.White
                        )
                    )
                }
            }

            if (loginState is LoginState.Error) {
                Text(
                    text = (loginState as LoginState.Error).message,
                    color = crema,
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.login(email, password) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Iniciar Sesión", color = naranjaPrincipal, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            TextButton(onClick = onRegisterClick, modifier = Modifier.padding(top = 8.dp)) {
                Text("¿No tienes cuenta? Regístrate aquí", color = Color.White)
            }
        }
    }
}