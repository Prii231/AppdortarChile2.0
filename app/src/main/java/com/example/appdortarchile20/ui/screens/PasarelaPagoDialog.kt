package com.example.appdortarchile20.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

// Formatea número de tarjeta con espacios cada 4 dígitos
private fun formatearTarjeta(input: String): String {
    val digitos = input.filter { it.isDigit() }.take(16)
    return digitos.chunked(4).joinToString(" ")
}

// Formatea fecha MM/YY
private fun formatearFecha(input: String): String {
    val digitos = input.filter { it.isDigit() }.take(4)
    return if (digitos.length >= 3) "${digitos.take(2)}/${digitos.drop(2)}" else digitos
}

// Detecta tipo de tarjeta según primeros dígitos
private fun tipoTarjeta(numero: String): String {
    val digitos = numero.filter { it.isDigit() }
    return when {
        digitos.startsWith("4")                          -> "Visa"
        digitos.startsWith("5") || digitos.startsWith("2") -> "Mastercard"
        digitos.startsWith("34") || digitos.startsWith("37") -> "Amex"
        else -> ""
    }
}

@Composable
fun PasarelaPagoDialog(
    campania: Campania,
    onDismiss: () -> Unit,
    onPagoExitoso: (monto: Int) -> Unit
) {
    var paso by remember { mutableStateOf(1) } // 1=monto, 2=tarjeta, 3=exito

    // Paso 1 — monto
    var montoSeleccionado by remember { mutableStateOf(5000) }
    var montoCustom by remember { mutableStateOf("") }

    // Paso 2 — datos tarjeta
    var nombreTitular by remember { mutableStateOf("") }
    var numeroTarjeta by remember { mutableStateOf("") }
    var fechaExp by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var procesando by remember { mutableStateOf(false) }

    val montoFinal = if (montoCustom.isNotEmpty()) montoCustom.toIntOrNull() ?: montoSeleccionado else montoSeleccionado
    val tarjetaValida = nombreTitular.length >= 3 &&
            numeroTarjeta.filter { it.isDigit() }.length == 16 &&
            fechaExp.filter { it.isDigit() }.length == 4 &&
            cvv.length >= 3

    Dialog(
        onDismissRequest = { if (!procesando) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {

                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            when (paso) {
                                1 -> "Elige el monto"
                                2 -> "Datos de pago"
                                else -> "¡Donación exitosa!"
                            },
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            campania.nombre,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    if (paso < 3) {
                        IconButton(onClick = onDismiss, enabled = !procesando) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar")
                        }
                    }
                }

                // Indicador de pasos
                if (paso < 3) {
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(1, 2).forEach { n ->
                            Surface(
                                modifier = Modifier.weight(1f).height(4.dp),
                                shape = RoundedCornerShape(2.dp),
                                color = if (paso >= n) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            ) {}
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // ── PASO 1: MONTO ──
                AnimatedVisibility(visible = paso == 1) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                        Text(
                            "Montos sugeridos",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Chips de monto
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(2000, 5000, 10000, 20000).forEach { monto ->
                                FilterChip(
                                    selected = montoSeleccionado == monto && montoCustom.isEmpty(),
                                    onClick = { montoSeleccionado = monto; montoCustom = "" },
                                    label = { Text("$${"%,d".format(monto)}") },
                                    modifier = Modifier.weight(1f),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                            }
                        }

                        Text(
                            "O ingresa un monto",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = montoCustom,
                            onValueChange = { montoCustom = it.filter { c -> c.isDigit() } },
                            label = { Text("Monto en CLP") },
                            leadingIcon = { Text("$", modifier = Modifier.padding(start = 12.dp), fontWeight = FontWeight.Bold) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        // Resumen
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Tu donación",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    "$${"%,d".format(montoFinal)} CLP",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(Modifier.height(4.dp))

                        Button(
                            onClick = { paso = 2 },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            enabled = montoFinal >= 500
                        ) {
                            Text("Continuar", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(8.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }

                        if (montoFinal < 500 && montoCustom.isNotEmpty()) {
                            Text(
                                "El monto mínimo es $500 CLP",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // ── PASO 2: TARJETA ──
                AnimatedVisibility(visible = paso == 2) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

                        // Tarjeta visual
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth().height(100.dp)
                        ) {
                            Box(modifier = Modifier.padding(16.dp)) {
                                Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            tipoTarjeta(numeroTarjeta).ifEmpty { "Tarjeta" },
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color.White.copy(alpha = 0.7f))
                                    }
                                    Text(
                                        if (numeroTarjeta.filter { it.isDigit() }.isEmpty()) "•••• •••• •••• ••••"
                                        else formatearTarjeta(numeroTarjeta),
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        letterSpacing = 2.sp
                                    )
                                }
                            }
                        }

                        // Campos
                        OutlinedTextField(
                            value = nombreTitular,
                            onValueChange = { input ->
                                // Solo letras y espacios, sin números ni caracteres especiales
                                val filtrado = input.filter { it.isLetter() || it == ' ' }
                                nombreTitular = filtrado.uppercase()
                            },
                            label = { Text("Nombre del titular") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                        )

                        OutlinedTextField(
                            value = formatearTarjeta(numeroTarjeta),
                            onValueChange = { raw ->
                                val digitos = raw.filter { it.isDigit() }.take(16)
                                numeroTarjeta = digitos
                            },
                            label = { Text("Número de tarjeta") },
                            leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                            trailingIcon = {
                                val tipo = tipoTarjeta(numeroTarjeta)
                                if (tipo.isNotEmpty()) {
                                    Text(tipo, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(end = 12.dp))
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = formatearFecha(fechaExp),
                                onValueChange = { raw ->
                                    val digitos = raw.filter { c -> c.isDigit() }.take(4)
                                    fechaExp = digitos
                                },
                                label = { Text("MM/AA") },
                                placeholder = { Text("12/27") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = cvv,
                                onValueChange = { cvv = it.filter { c -> c.isDigit() }.take(4) },
                                label = { Text("CVV") },
                                trailingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp)) },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                        }

                        // Resumen monto
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total a donar", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("$${"%,d".format(montoFinal)} CLP", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = { paso = 1 },
                                modifier = Modifier.weight(1f).height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                enabled = !procesando
                            ) {
                                Text("Volver")
                            }

                            Button(
                                onClick = {
                                    procesando = true
                                },
                                modifier = Modifier.weight(2f).height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                enabled = tarjetaValida && !procesando
                            ) {
                                if (procesando) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Procesando...", fontWeight = FontWeight.Bold)
                                } else {
                                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Pagar ahora", fontWeight = FontWeight.Bold)
                                }
                            }

                            // Delay de 2 segundos antes de mostrar éxito
                            LaunchedEffect(procesando) {
                                if (procesando) {
                                    kotlinx.coroutines.delay(2000)
                                    paso = 3
                                    onPagoExitoso(montoFinal)
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(12.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "Pago simulado — datos no reales",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // ── PASO 3: ÉXITO ──
                AnimatedVisibility(visible = paso == 3) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Spacer(Modifier.height(8.dp))

                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier.size(80.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Icon(
                                    Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }

                        Text(
                            "¡Muchas gracias!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            "Tu donación de $${"%,d".format(montoFinal)} CLP a \"${campania.nombre}\" fue procesada con éxito.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                FilaDato("Campaña", campania.nombre)
                                FilaDato("Monto donado", "$${"%,d".format(montoFinal)} CLP")
                                FilaDato("Titular", nombreTitular)
                                FilaDato("Tarjeta", "**** **** **** ${numeroTarjeta.takeLast(4)}")
                            }
                        }

                        Spacer(Modifier.height(4.dp))

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Cerrar", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilaDato(label: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
        Text(valor, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}