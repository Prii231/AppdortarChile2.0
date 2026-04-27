package com.example.appdortarchile20.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.appdortarchile20.data.ChileData
import com.example.appdortarchile20.data.model.Pet
import com.example.appdortarchile20.ui.viewmodel.PetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdoptarScreen(viewModel: PetViewModel) {
    val pets by viewModel.allPets.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    // Estados de filtros
    var filterType by remember { mutableStateOf("Todos") }
    var filterRegion by remember { mutableStateOf("Todas las Regiones") }
    var expandedRegion by remember { mutableStateOf(false) }
    var selectedPet by remember { mutableStateOf<Pet?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {

        // 1. Selector de Región (Menú Desplegable)
        ExposedDropdownMenuBox(
            expanded = expandedRegion,
            onExpandedChange = { expandedRegion = !expandedRegion },
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            OutlinedTextField(
                value = filterRegion,
                onValueChange = {},
                readOnly = true,
                label = { Text("Filtrar por Ubicación") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRegion) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandedRegion,
                onDismissRequest = { expandedRegion = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Todas las Regiones") },
                    onClick = {
                        filterRegion = "Todas las Regiones"
                        expandedRegion = false
                    }
                )
                ChileData.regionesChile.forEach { region ->
                    DropdownMenuItem(
                        text = { Text(region) },
                        onClick = {
                            filterRegion = region
                            expandedRegion = false
                        }
                    )
                }
            }
        }

        // 2. Chips de Tipo de Mascota (Scroll Horizontal)
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .horizontalScroll(rememberScrollState())
        ) {
            listOf("Todos", "Perro", "Gato", "Otro").forEach { type ->
                FilterChip(
                    selected = filterType == type,
                    onClick = { filterType = type },
                    label = { Text(type) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }

        // 3. Lista de Mascotas Filtrada
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            val filteredList = pets.filter { pet ->
                val matchesType = if (filterType == "Todos") true else pet.type == filterType
                val matchesRegion = if (filterRegion == "Todas las Regiones") true else pet.region == filterRegion
                matchesType && matchesRegion
            }

            items(filteredList) { pet ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    onClick = { selectedPet = pet }
                ) {
                    Column {
                        AsyncImage(
                            model = pet.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().height(160.dp),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(pet.name, style = MaterialTheme.typography.headlineSmall)
                                Text("${pet.type} • ${pet.region}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            // Solo el dueño puede eliminar su publicación
                            if (currentUser != null && pet.ownerEmail == currentUser!!.email) {
                                IconButton(onClick = { viewModel.deletePet(pet) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedPet != null) {
        PetDetailDialog(pet = selectedPet!!, onDismiss = { selectedPet = null })
    }
}