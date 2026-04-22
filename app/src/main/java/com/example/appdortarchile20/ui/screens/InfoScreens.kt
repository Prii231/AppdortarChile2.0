package com.example.appdortarchile20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// --- PANTALLA NOSOTROS ---
@Composable
fun NosotrosScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Nuestra Misión",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Appdoptar Chile es una plataforma dedicada a conectar animales rescatados con familias responsables en todo el territorio nacional.",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Creemos firmemente en la tenencia responsable y en que cada mascota merece una segunda oportunidad. Trabajamos para facilitar el proceso de adopción y dar visibilidad a quienes no tienen voz.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

// --- PANTALLA BLOG ---
@Composable
fun BlogScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Consejos y Artículos",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        BlogItem(
            titulo = "10 tips para el primer día",
            contenido = "Prepara un espacio tranquilo, deja que explore a su ritmo y mantén la calma..."
        )
        BlogItem(
            titulo = "Importancia de la Vacunación",
            contenido = "Mantener el calendario al día protege a tu mascota de enfermedades graves como el parvovirus..."
        )
        BlogItem(
            titulo = "Ley de Tenencia Responsable",
            contenido = "Conoce tus deberes como tutor: registro, cuidados de salud y seguridad en espacios públicos."
        )
    }
}

@Composable
fun BlogItem(titulo: String, contenido: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = contenido, style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = { /* Leer más */ }, modifier = Modifier.align(Alignment.End)) {
                Text("Leer más")
            }
        }
    }
}

// --- PANTALLA CONTACTO ---
@Composable
fun ContactoScreen() {
    var nombre by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Contáctanos",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text("Si eres un refugio o tienes dudas, envíanos un mensaje.")

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Tu Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = mensaje,
            onValueChange = { mensaje = it },
            label = { Text("Mensaje o sugerencia") },
            modifier = Modifier.fillMaxWidth().height(150.dp),
            minLines = 5
        )

        Button(
            onClick = { /* Lógica de envío */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar Formulario")
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text("Email: contacto@appdoptarchile.cl", style = MaterialTheme.typography.bodySmall)
        Text("Instagram: @appdoptarchile", style = MaterialTheme.typography.bodySmall)
    }
}