package com.example.appdortarchile20.ui.model

import org.osmdroid.util.GeoPoint

data class UrgenciaReporte(
    val id: String = java.util.UUID.randomUUID().toString(),
    val titulo: String = "",
    val descripcion: String = "",
    val tipo: TipoUrgencia = TipoUrgencia.EXTRAVIADO,
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val horaReporte: Long = System.currentTimeMillis(),
    val imagenUrl: String? = null
) {
    fun toGeoPoint(): GeoPoint = GeoPoint(latitud, longitud)
}

// Cambiamos 'Int' por 'Long' para que acepte los códigos hexadecimales ARGB
enum class TipoUrgencia(val descripcion: String, val colorHex: Long) {
    EXTRAVIADO("Mascota Perdida", 0xFFFFD700), // Amarillo/Dorado
    HERIDO("Mascota Herida", 0xFFEF5350),    // Rojo
    HAMBRE("Necesita Alimento", 0xFF4CAF50),  // Verde
    MALTRATO("Denuncia Maltrato", 0xFF9C27B0) // Morado
}