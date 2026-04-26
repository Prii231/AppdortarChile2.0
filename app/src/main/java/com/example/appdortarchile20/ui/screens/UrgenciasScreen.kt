package com.example.appdortarchile20.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.example.appdortarchile20.data.model.TipoUrgencia
import com.example.appdortarchile20.data.model.UrgenciaReporte
import com.example.appdortarchile20.ui.viewmodel.PetViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

// Función auxiliar para crear un bitmap circular de color para el marcador
fun createColoredMarkerBitmap(colorHex: Long): Bitmap {
    val size = 60
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorHex.toInt()
    }
    val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 4f, paint)
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 4f, strokePaint)
    return bitmap
}

@Composable
fun UrgenciasScreen(viewModel: PetViewModel) {
    val reportes by viewModel.allReportes.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }

    // Efecto que se dispara cada vez que cambia la lista de reportes
    LaunchedEffect(reportes) {
        val mapView = mapViewRef ?: return@LaunchedEffect
        mapView.overlays.clear()

        reportes.forEach { reporte ->
            val tipo = TipoUrgencia.entries.find { it.name == reporte.tipo } ?: TipoUrgencia.EXTRAVIADO
            val marker = Marker(mapView).apply {
                position = GeoPoint(reporte.latitud, reporte.longitud)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = reporte.titulo
                snippet = reporte.descripcion

                // Aplicar bitmap de color según el tipo de urgencia
                val bmp = createColoredMarkerBitmap(tipo.colorHex)
                icon = android.graphics.drawable.BitmapDrawable(
                    mapView.context.resources,
                    bmp
                )
            }
            mapView.overlays.add(marker)
        }
        mapView.invalidate()
    }

    if (showDialog) {
        CrearAlertaDialog(
            onDismiss = { showDialog = false },
            onConfirm = { reporte -> viewModel.addReporte(reporte) }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Color(0xFFEF5350),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.AddLocation, contentDescription = "Reportar urgencia")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            AndroidView(
                factory = { context ->
                    MapView(context).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(12.0)
                        controller.setCenter(GeoPoint(-33.4489, -70.6693)) // Santiago
                    }.also { mapViewRef = it }
                },
                update = { mapView ->
                    mapViewRef = mapView
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}