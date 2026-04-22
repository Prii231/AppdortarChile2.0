package com.example.appdortarchile20.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.appdortarchile20.ui.viewmodel.PetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDrawerScreen(viewModel: PetViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Cambiamos el estado inicial a "adoptar" o la que prefieras
    var currentScreen by remember { mutableStateOf("adoptar") }

    val azulInstitucional = Color(0xFF003399)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFFFDFDFD),
                modifier = Modifier.width(310.dp)
            ) {
                // --- CABECERA (LOGO UNIFORME) ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(azulInstitucional, Color(0xFF0055CC))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("App", fontSize = 26.sp, fontWeight = FontWeight.Black, color = Color.White)
                            Text("Doptar", fontSize = 26.sp, fontWeight = FontWeight.Black, color = Color(0xFF99CCFF))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Chile", fontSize = 26.sp, fontWeight = FontWeight.Black, color = Color(0xFFEF5350))
                        }
                        Text(
                            text = "Encuentra tu alma gemela <3",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- OPCIONES DEL MENÚ ---

                // Item: Adoptar
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Pets, contentDescription = null, tint = azulInstitucional) },
                    label = { Text("Adoptar", fontWeight = FontWeight.SemiBold) },
                    selected = currentScreen == "adoptar",
                    onClick = {
                        currentScreen = "adoptar"
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                // NUEVO ITEM: URGENCIAS (MAPA)
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red) },
                    label = { Text("Urgencias", fontWeight = FontWeight.ExtraBold, color = Color.Red) },
                    selected = currentScreen == "urgencias",
                    onClick = {
                        currentScreen = "urgencias"
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.AddCircle, contentDescription = null, tint = azulInstitucional) },
                    label = { Text("Dar en Adopción", fontWeight = FontWeight.SemiBold) },
                    selected = currentScreen == "dar",
                    onClick = {
                        currentScreen = "dar"
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = null, tint = azulInstitucional) },
                    label = { Text("Blog", fontWeight = FontWeight.SemiBold) },
                    selected = currentScreen == "blog",
                    onClick = {
                        currentScreen = "blog"
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp, horizontal = 28.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = null, tint = azulInstitucional) },
                    label = { Text("Nosotros", fontWeight = FontWeight.SemiBold) },
                    selected = currentScreen == "nosotros",
                    onClick = {
                        currentScreen = "nosotros"
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Email, contentDescription = null, tint = azulInstitucional) },
                    label = { Text("Contacto", fontWeight = FontWeight.SemiBold) },
                    selected = currentScreen == "contacto",
                    onClick = {
                        currentScreen = "contacto"
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = when(currentScreen) {
                                "adoptar" -> "Mascotas en Adopción"
                                "urgencias" -> "Mapa de Urgencias"
                                "dar" -> "Publicar Mascota"
                                "blog" -> "Consejos y Blog"
                                "nosotros" -> "Nuestra Misión"
                                else -> "Contacto"
                            },
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = azulInstitucional,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when (currentScreen) {
                    "adoptar" -> AdoptarScreen(viewModel)
                    "urgencias" -> UrgenciasScreen() // Llamada a tu nueva pantalla de mapa
                    "dar" -> DarAdopcionScreen(viewModel, onSaved = { currentScreen = "adoptar" })
                    "blog" -> BlogScreen()
                    "nosotros" -> NosotrosScreen()
                    "contacto" -> ContactoScreen()
                }
            }
        }
    }
}