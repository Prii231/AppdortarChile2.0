package com.example.appdortarchile20.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

    var busqueda by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf("Todos") }
    var filterRegion by remember { mutableStateOf("Todas las Regiones") }
    var soloMias by remember { mutableStateOf(false) }
    var expandedRegion by remember { mutableStateOf(false) }
    var selectedPet by remember { mutableStateOf<Pet?>(null) }
    var petAEliminar by remember { mutableStateOf<Pet?>(null) }

    // Lista filtrada combinando búsqueda + tipo + región + mis mascotas
    val filteredList = pets.filter { pet ->
        val matchesSearch = busqueda.isEmpty() ||
                pet.name.contains(busqueda, ignoreCase = true)
        val matchesType = filterType == "Todos" || pet.type == filterType
        val matchesRegion = filterRegion == "Todas las Regiones" || pet.region == filterRegion
        val matchesMias = !soloMias || pet.ownerEmail == currentUser?.email
        matchesSearch && matchesType && matchesRegion && matchesMias
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // 1. Campo de búsqueda
        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            placeholder = { Text("Buscar por nombre...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary)
            },
            trailingIcon = {
                if (busqueda.isNotEmpty()) {
                    IconButton(onClick = { busqueda = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Limpiar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            },
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        // 2. Selector de Región
        ExposedDropdownMenuBox(
            expanded = expandedRegion,
            onExpandedChange = { expandedRegion = !expandedRegion },
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()
        ) {
            OutlinedTextField(
                value = filterRegion,
                onValueChange = {},
                readOnly = true,
                label = { Text("Filtrar por región") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRegion) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )
            ExposedDropdownMenu(
                expanded = expandedRegion,
                onDismissRequest = { expandedRegion = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Todas las Regiones") },
                    onClick = { filterRegion = "Todas las Regiones"; expandedRegion = false }
                )
                ChileData.regionesChile.forEach { region ->
                    DropdownMenuItem(
                        text = { Text(region) },
                        onClick = { filterRegion = region; expandedRegion = false }
                    )
                }
            }
        }

        // 3. Chips de tipo + Mis mascotas
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .horizontalScroll(rememberScrollState())
        ) {
            listOf("Todos", "Perro", "Gato", "Otro").forEach { type ->
                FilterChip(
                    selected = filterType == type,
                    onClick = { filterType = type },
                    label = { Text(type, fontWeight = if (filterType == type) FontWeight.SemiBold else FontWeight.Normal) },
                    modifier = Modifier.padding(end = 8.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
            // Chip Mis mascotas — solo visible si hay usuario logueado
            if (currentUser != null) {
                FilterChip(
                    selected = soloMias,
                    onClick = { soloMias = !soloMias },
                    label = {
                        Text(
                            "Mis mascotas",
                            fontWeight = if (soloMias) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = if (soloMias) {
                        {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else null,
                    modifier = Modifier.padding(end = 8.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
            }
        }

        // 4. Contador de resultados
        Text(
            text = when (filteredList.size) {
                0 -> "Sin resultados"
                1 -> "1 mascota encontrada"
                else -> "${filteredList.size} mascotas encontradas"
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
        )

        // 5. Lista o estado vacío
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text("🐾", style = MaterialTheme.typography.displayMedium)
                    Text(
                        if (busqueda.isNotEmpty())
                            "No encontramos mascotas con el nombre \"$busqueda\""
                        else
                            "No hay mascotas disponibles con estos filtros",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    if (busqueda.isNotEmpty() || filterType != "Todos" || filterRegion != "Todas las Regiones" || soloMias) {
                        TextButton(onClick = {
                            busqueda = ""
                            filterType = "Todos"
                            filterRegion = "Todas las Regiones"
                            soloMias = false
                        }) {
                            Text("Limpiar filtros", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredList) { pet ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { selectedPet = pet },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column {
                            AsyncImage(
                                model = pet.imageUrl,
                                contentDescription = pet.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(170.dp)
                                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(pet.name, style = MaterialTheme.typography.titleLarge)
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = RoundedCornerShape(20.dp)
                                        ) {
                                            Text(
                                                pet.type,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                        Text(
                                            pet.region,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                if (currentUser != null && pet.ownerEmail == currentUser!!.email) {
                                    IconButton(onClick = { petAEliminar = pet }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Borrar",
                                            tint = MaterialTheme.colorScheme.error)
                                    }
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

    if (petAEliminar != null) {
        AlertDialog(
            onDismissRequest = { petAEliminar = null },
            icon = {
                Icon(Icons.Default.Delete, contentDescription = null,
                    tint = MaterialTheme.colorScheme.error)
            },
            title = { Text("¿Eliminar publicación?", fontWeight = FontWeight.Bold) },
            text = {
                Text("¿Estás seguro de que deseas eliminar a ${petAEliminar!!.name}? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deletePet(petAEliminar!!)
                        petAEliminar = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { petAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}