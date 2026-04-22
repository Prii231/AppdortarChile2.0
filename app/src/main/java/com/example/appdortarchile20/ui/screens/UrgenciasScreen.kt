package com.example.appdortarchile20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun UrgenciasScreen() {
    val azulInstitucional = Color(0xFF003399)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Aquí abriremos el formulario después */ },
                containerColor = Color(0xFFEF5350),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.AddLocation, contentDescription = "Reportar")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // MAPA DE OPENSTREETMAP
            AndroidView(
                factory = { context ->
                    MapView(context).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)

                        // Configuración inicial de la cámara
                        controller.setZoom(15.0)
                        val startPoint = GeoPoint(-33.4489, -70.6693) // Santiago
                        controller.setCenter(startPoint)

                        // Ejemplo de un marcador (Pin)
                        val marker = Marker(this)
                        marker.position = startPoint
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        marker.title = "Perrito en urgencia"
                        marker.snippet = "Se busca dueño temporal"
                        overlays.add(marker)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}