package com.example.appdortarchile20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.appdortarchile20.data.model.Pet

@Composable
fun PetDetailDialog(pet: Pet, onDismiss: () -> Unit) {
    var showContact by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = { showContact = true }) { Text("Contactar") }
        },
        title = { Text(pet.name) },
        text = {
            Column {
                AsyncImage(model = pet.imageUrl, contentDescription = null, modifier = Modifier.fillMaxWidth().height(180.dp))
                Text("Ubicación: ${pet.city}")
                Text("Descripción: ${pet.description}")
                if (showContact) {
                    Divider(Modifier.padding(vertical = 8.dp))
                    Text("Dueño: ${pet.ownerName}", style = MaterialTheme.typography.titleMedium)
                    Text("WhatsApp: ${pet.ownerPhone}", style = MaterialTheme.typography.headlineSmall)
                }
            }
        }
    )
}