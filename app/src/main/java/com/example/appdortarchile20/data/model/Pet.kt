package com.example.appdortarchile20.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pets")
data class Pet(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val type: String,          // Perro, Gato u Otro
    val age: String,           // Edad aproximada
    val region: String,        // Región de Chile
    val city: String,          // Ciudad
    val imageUrl: String,      // Link a la foto
    val hasVaccines: Boolean,  // Si tiene o no vacunas
    val isSterilized: Boolean, // Si está o no operado
    val description: String,   // Descripción de la mascota
    val ownerName: String,     // Nombre de quien da en adopción
    val ownerPhone: String     // Teléfono de contacto
)