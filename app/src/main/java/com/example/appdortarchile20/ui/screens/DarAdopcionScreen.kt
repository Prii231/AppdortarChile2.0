package com.example.appdortarchile20.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.appdortarchile20.data.ChileData
import com.example.appdortarchile20.data.model.Pet
import com.example.appdortarchile20.ui.viewmodel.PetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarAdopcionScreen(viewModel: PetViewModel, onSaved: () -> Unit) {
    // Estados del formulario
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Perro") }
    var age by remember { mutableStateOf("") }
    var selectedRegion by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var hasVaccines by remember { mutableStateOf(false) }
    var isSterilized by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }

    // Estado para la imagen seleccionada
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Lanzador para abrir la galería
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> imageUri = uri }
    )

    // Usuario autenticado
    val currentUser by viewModel.currentUser.collectAsState()

    var expandedRegion by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Publicar Mascota", style = MaterialTheme.typography.headlineMedium)

        // --- SECCIÓN DE FOTO ---
        Card(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            onClick = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                if (imageUri == null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(48.dp))
                        Text("Toca para subir foto")
                    }
                } else {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Foto seleccionada",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        // --- DATOS BÁSICOS ---
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre de la mascota") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Tipo de animal:", style = MaterialTheme.typography.titleMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            listOf("Perro", "Gato", "Otro").forEach { option ->
                RadioButton(selected = type == option, onClick = { type = option })
                Text(option, modifier = Modifier.padding(end = 16.dp))
            }
        }

        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Edad (ej: 2 años)") },
            modifier = Modifier.fillMaxWidth()
        )

        // --- UBICACIÓN ---
        ExposedDropdownMenuBox(
            expanded = expandedRegion,
            onExpandedChange = { expandedRegion = !expandedRegion }
        ) {
            OutlinedTextField(
                value = selectedRegion,
                onValueChange = {},
                readOnly = true,
                label = { Text("Región") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRegion) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expandedRegion, onDismissRequest = { expandedRegion = false }) {
                ChileData.regionesChile.forEach { region ->
                    DropdownMenuItem(
                        text = { Text(region) },
                        onClick = { selectedRegion = region; expandedRegion = false }
                    )
                }
            }
        }

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("Ciudad/Comuna") },
            modifier = Modifier.fillMaxWidth()
        )

        // --- SALUD ---
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Checkbox(checked = hasVaccines, onCheckedChange = { hasVaccines = it })
            Text("Vacunas al día")
            Spacer(Modifier.width(20.dp))
            Checkbox(checked = isSterilized, onCheckedChange = { isSterilized = it })
            Text("Esterilizado")
        }

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth().height(120.dp)
        )

        // --- BOTÓN FINAL ---
        Button(
            onClick = {
                val newPet = Pet(
                    id = 0,
                    name = name,
                    type = type,
                    age = age,
                    region = selectedRegion,
                    city = city,
                    imageUrl = imageUri?.toString() ?: "https://placedog.net/500",
                    hasVaccines = hasVaccines,
                    isSterilized = isSterilized,
                    description = description,
                    ownerName = currentUser?.name ?: "Anónimo",
                    ownerPhone = currentUser?.phone ?: ""
                )
                viewModel.addPet(newPet)
                onSaved()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotEmpty() && selectedRegion.isNotEmpty() && imageUri != null
        ) {
            Text("Publicar Mascota")
        }
    }
}