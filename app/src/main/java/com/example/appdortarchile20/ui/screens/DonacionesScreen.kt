package com.example.appdortarchile20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

// Modelo de campaña (MVP: datos estáticos)
data class Campania(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val metaMonto: Int,
    val montoRecaudado: Int,
    val imagenUrl: String
)

// Campañas de ejemplo hardcodeadas para el MVP
val campaniasMVP = listOf(
    Campania(
        id = 1,
        nombre = "Cirugía para Luna",
        descripcion = "Luna es una perrita atropellada que necesita una operación de urgencia en su pata trasera.",
        metaMonto = 300_000,
        montoRecaudado = 187_000,
        imagenUrl = "https://placedog.net/600/300"
    ),
    Campania(
        id = 2,
        nombre = "Alimentación Refugio Norte",
        descripcion = "El Refugio Esperanza de Antofagasta necesita alimento para sus 45 animales este mes.",
        metaMonto = 150_000,
        montoRecaudado = 98_500,
        imagenUrl = "https://placedog.net/601/300"
    ),
    Campania(
        id = 3,
        nombre = "Vacunación Colonia Felina",
        descripcion = "Campaña para vacunar a 30 gatos callejeros del sector Macul en Santiago.",
        metaMonto = 80_000,
        montoRecaudado = 80_000,
        imagenUrl = "https://placekitten.com/600/300"
    ),
    Campania(
        id = 4,
        nombre = "Rehabilitación Firulais",
        descripcion = "Firulais sobrevivió a un incendio y necesita tratamientos de rehabilitación durante 3 meses.",
        metaMonto = 500_000,
        montoRecaudado = 62_000,
        imagenUrl = "https://placedog.net/602/300"
    )
)

@Composable
fun DonacionesScreen() {
    val azulInstitucional = Color(0xFF003399)

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Campañas de Donación",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Ayuda a financiar el cuidado de animales en situación vulnerable.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        items(campaniasMVP) { campania ->
            CampaniaCard(campania = campania, colorPrincipal = azulInstitucional)
        }
    }
}

@Composable
fun CampaniaCard(campania: Campania, colorPrincipal: Color) {
    val progreso = (campania.montoRecaudado.toFloat() / campania.metaMonto.toFloat()).coerceIn(0f, 1f)
    val completada = progreso >= 1f

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column {
            // Imagen de la campaña
            AsyncImage(
                model = campania.imagenUrl,
                contentDescription = campania.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {
                // Nombre
                Text(
                    text = campania.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Descripción
                Text(
                    text = campania.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Barra de progreso
                LinearProgressIndicator(
                    progress = { progreso },
                    modifier = Modifier.fillMaxWidth(),
                    color = if (completada) Color(0xFF4CAF50) else colorPrincipal,
                    trackColor = Color(0xFFE0E0E0)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Montos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${"%.0f".format(campania.montoRecaudado.toFloat())} CLP",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (completada) Color(0xFF4CAF50) else colorPrincipal
                    )
                    Text(
                        text = "Meta: $${"%.0f".format(campania.metaMonto.toFloat())} CLP",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botón Donar
                Button(
                    onClick = { /* MVP: sin integración de pago real */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (completada) Color(0xFF4CAF50) else colorPrincipal
                    ),
                    enabled = !completada
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (completada) "¡Meta alcanzada! 🎉" else "Donar a esta campaña")
                }
            }
        }
    }
}