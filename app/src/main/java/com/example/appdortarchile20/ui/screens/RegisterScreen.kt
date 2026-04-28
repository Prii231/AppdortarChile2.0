package com.example.appdortarchile20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.appdortarchile20.data.model.User
import com.example.appdortarchile20.ui.viewmodel.PetViewModel
import com.example.appdortarchile20.ui.viewmodel.LoginState
import com.example.appdortarchile20.data.ChileData

// Funciones de validación
private fun validarEmail(email: String): String? {
    if (email.isEmpty()) return "El correo es obligatorio"
    val regex = Regex("^[^@]+@[^@]+\\.[a-zA-Z]{2,}$")
    if (!regex.matches(email)) return "Ingresa un correo válido (ej: usuario@mail.com)"
    return null
}

private fun validarPassword(password: String): String? {
    if (password.isEmpty()) return "La contraseña es obligatoria"
    if (password.length < 6) return "Mínimo 6 caracteres"
    return null
}

private fun validarNombre(nombre: String): String? {
    if (nombre.isEmpty()) return "El nombre es obligatorio"
    if (nombre.trim().length < 3) return "Mínimo 3 caracteres"
    if (!nombre.all { it.isLetter() || it.isWhitespace() }) return "Solo se permiten letras"
    return null
}

private fun validarTelefono(telefono: String): String? {
    if (telefono.length != 8) return "Ingresa los 8 dígitos de tu celular"
    return null
}

private fun validarEdad(edad: String): String? {
    if (edad.isEmpty()) return null // opcional
    val num = edad.toIntOrNull() ?: return "Ingresa solo números"
    if (num < 1 || num > 120) return "Ingresa una edad válida (1-120)"
    return null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(viewModel: PetViewModel, onRegistered: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") } // Solo los 8 dígitos
    var selectedRegion by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // Mostrar errores solo después del primer intento
    var intentado by remember { mutableStateOf(false) }

    val loginState by viewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) onRegistered()
    }

    // Errores de validación
    val errorNombre = if (intentado) validarNombre(name) else null
    val errorTelefono = if (intentado) validarTelefono(phone) else null
    val errorEdad = if (intentado) validarEdad(age) else null
    val errorEmail = if (intentado) validarEmail(email) else null
    val errorPassword = if (intentado) validarPassword(password) else null
    val errorRegion = if (intentado && selectedRegion.isEmpty()) "Selecciona una región" else null

    val formularioValido = validarNombre(name) == null &&
            validarTelefono(phone) == null &&
            validarEdad(age) == null &&
            validarEmail(email) == null &&
            validarPassword(password) == null &&
            selectedRegion.isNotEmpty()

    FondoHuellas {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Completa tus datos para empezar a adoptar.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))

            // Nombre
            OutlinedTextField(
                value = name,
                onValueChange = { name = it.filter { c -> c.isLetter() || c.isWhitespace() } },
                label = { Text("Nombre completo") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                isError = errorNombre != null,
                supportingText = { if (errorNombre != null) Text(errorNombre, color = MaterialTheme.colorScheme.error) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            // Teléfono con prefijo +569 fijo e imborrable
            OutlinedTextField(
                value = phone,
                onValueChange = { input ->
                    // Solo guardar dígitos, máximo 8
                    val soloDigitos = input.filter { it.isDigit() }.take(8)
                    phone = soloDigitos
                },
                label = { Text("Teléfono celular") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                prefix = { Text("+569 ", color = MaterialTheme.colorScheme.onSurface) },
                isError = errorTelefono != null,
                supportingText = {
                    if (errorTelefono != null) Text(errorTelefono, color = MaterialTheme.colorScheme.error)
                    else Text("${phone.length}/8 dígitos", color = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            // Región
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedRegion,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Selecciona tu región") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    isError = errorRegion != null,
                    supportingText = { if (errorRegion != null) Text(errorRegion, color = MaterialTheme.colorScheme.error) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    ChileData.regionesChile.forEach { region ->
                        DropdownMenuItem(
                            text = { Text(region) },
                            onClick = { selectedRegion = region; expanded = false }
                        )
                    }
                }
            }

            // Edad
            OutlinedTextField(
                value = age,
                onValueChange = { age = it.filter { c -> c.isDigit() }.take(3) },
                label = { Text("Edad") },
                leadingIcon = { Icon(Icons.Default.Cake, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                isError = errorEdad != null,
                supportingText = { if (errorEdad != null) Text(errorEdad, color = MaterialTheme.colorScheme.error) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it.trim() },
                label = { Text("Correo electrónico") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                isError = errorEmail != null,
                supportingText = { if (errorEmail != null) Text(errorEmail, color = MaterialTheme.colorScheme.error) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            // Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                visualTransformation = PasswordVisualTransformation(),
                isError = errorPassword != null,
                supportingText = {
                    if (errorPassword != null) Text(errorPassword, color = MaterialTheme.colorScheme.error)
                    else Text("Mínimo 6 caracteres", color = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            // Error de Room (email duplicado etc)
            if (loginState is LoginState.Error) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        (loginState as LoginState.Error).message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            Button(
                onClick = {
                    intentado = true
                    if (formularioValido) {
                        viewModel.register(
                            User(
                                id = 0,
                                name = name.trim(),
                                phone = "+569${phone.trim()}",
                                region = selectedRegion,
                                age = age.toIntOrNull() ?: 0,
                                email = email.trim(),
                                password = password
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Crear mi cuenta", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}