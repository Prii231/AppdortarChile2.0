package com.example.appdortarchile20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appdortarchile20.data.model.TipoUrgencia
import com.example.appdortarchile20.data.model.UrgenciaReporte

@Composable
fun CrearAlertaDialog(
    onDismiss: () -> Unit,
    onConfirm: (UrgenciaReporte) -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var tipoSeleccionado by remember { mutableStateOf(TipoUrgencia.EXTRAVIADO) }

    // Latitud/longitud por defecto: centro de Santiago
    // En el Paso 5 esto se conectará con el mapa real
    val latitud = -33.4489
    val longitud = -70.6693

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Reportar Urgencia", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // Campo Título
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título del reporte") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Campo Descripción
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    maxLines = 4
                )

                // Selector de Tipo
                Text("Tipo de urgencia:", style = MaterialTheme.typography.titleSmall)
                Column {
                    TipoUrgencia.entries.forEach { tipo ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = tipoSeleccionado == tipo,
                                onClick = { tipoSeleccionado = tipo }
                            )
                            Column {
                                Text(
                                    text = tipo.descripcion,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val reporte = UrgenciaReporte(
                        titulo = titulo,
                        descripcion = descripcion,
                        tipo = tipoSeleccionado.name,
                        latitud = latitud,
                        longitud = longitud
                    )
                    onConfirm(reporte)
                    onDismiss()
                },
                enabled = titulo.isNotEmpty()
            ) {
                Text("Reportar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}