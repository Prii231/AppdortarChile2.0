package com.example.appdortarchile20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appdortarchile20.data.model.User
import com.example.appdortarchile20.ui.viewmodel.PetViewModel
import com.example.appdortarchile20.ui.viewmodel.LoginState // Asegúrate de importar esto
import com.example.appdortarchile20.data.ChileData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(viewModel: PetViewModel, onRegistered: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedRegion by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // --- NUEVO: Observar el estado del registro ---
    val loginState by viewModel.loginState.collectAsState()

    // Este bloque detecta cuando el registro fue exitoso y gatilla la navegación
    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            onRegistered() // Llama a la navegación hacia atrás o al Home
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Registro de Usuario", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre Completo") }, modifier = Modifier.fillMaxWidth())

        // --- Agregado: Campo de Teléfono ---
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Teléfono (+569...)") }, modifier = Modifier.fillMaxWidth())

        // --- Selector de Región ---
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedRegion,
                onValueChange = {},
                readOnly = true,
                label = { Text("Selecciona tu Región") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                ChileData.regionesChile.forEach { region ->
                    DropdownMenuItem(
                        text = { Text(region) },
                        onClick = {
                            selectedRegion = region
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Edad") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth())

        // Muestra un mensaje de error si el registro falla
        if (loginState is LoginState.Error) {
            Text(
                text = (loginState as LoginState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Button(
            onClick = {
                // Solo registramos si los campos básicos no están vacíos
                if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    viewModel.register(
                        User(
                            id = 0,
                            name = name,
                            phone = phone,
                            region = selectedRegion,
                            age = age.toIntOrNull() ?: 0,
                            email = email,
                            password = password
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            // El botón se habilita solo si hay nombre, región y correo
            enabled = name.isNotEmpty() && selectedRegion.isNotEmpty() && email.isNotEmpty()
        ) {
            Text("Finalizar Registro")
        }
    }
}