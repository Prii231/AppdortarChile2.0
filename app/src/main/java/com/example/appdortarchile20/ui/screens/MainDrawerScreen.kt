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
    var currentScreen by remember { mutableStateOf("adoptar") }

    // Paleta cálida
    val naranjaPrincipal = Color(0xFFE85D04)
    val naranjaClaro     = Color(0xFFFB8500)
    val verdeSalvia      = Color(0xFF4A7C59)
    val crema            = Color(0xFFFFF5EB)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFFFFFBF5),
                modifier = Modifier.width(310.dp)
            ) {
                // Cabecera con gradiente cálido
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(naranjaPrincipal, naranjaClaro)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("App",    fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.White)
                            Text("Doptar", fontSize = 28.sp, fontWeight = FontWeight.Black, color = crema)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Chile",  fontSize = 28.sp, fontWeight = FontWeight.Black, color = verdeSalvia)
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

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Pets, contentDescription = null, tint = naranjaPrincipal) },
                    label = { Text("Adoptar", fontWeight = FontWeight.SemiBold) },
                    selected = currentScreen == "adoptar",
                    onClick = { currentScreen = "adoptar"; scope.launch { drawerState.close() } },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Color(0xFFFFF0E0)
                    ),
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD32F2F)) },
                    label = { Text("Urgencias", fontWeight = FontWeight.ExtraBold, color = Color(0xFFD32F2F)) },
                    selected = currentScreen == "urgencias",
                    onClick = { currentScreen = "urgencias"; scope.launch { drawerState.close() } },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Color(0xFFFFEBEE)
                    ),
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFE85D04)) },
                    label = { Text("Donaciones", fontWeight = FontWeight.SemiBold) },
                    selected = currentScreen == "donaciones",
                    onClick = { currentScreen = "donaciones"; scope.launch { drawerState.close() } },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Color(0xFFFFF0E0)
                    ),
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.AddCircle, contentDescription = null, tint = verdeSalvia) },
                    label = { Text("Dar en Adopción", fontWeight = FontWeight.SemiBold) },
                    selected = currentScreen == "dar",
                    onClick = { currentScreen = "dar"; scope.launch { drawerState.close() } },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Color(0xFFEDF5EF)
                    ),
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = null, tint = naranjaPrincipal) },
                    label = { Text("Blog", fontWeight = FontWeight.SemiBold) },
                    selected = currentScreen == "blog",
                    onClick = { currentScreen = "blog"; scope.launch { drawerState.close() } },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Color(0xFFFFF0E0)
                    ),
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 28.dp),
                    color = Color(0xFFEED8C0)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = null, tint = naranjaPrincipal) },
                    label = { Text("Nosotros", fontWeight = FontWeight.SemiBold) },
                    selected = currentScreen == "nosotros",
                    onClick = { currentScreen = "nosotros"; scope.launch { drawerState.close() } },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Color(0xFFFFF0E0)
                    ),
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Email, contentDescription = null, tint = naranjaPrincipal) },
                    label = { Text("Contacto", fontWeight = FontWeight.SemiBold) },
                    selected = currentScreen == "contacto",
                    onClick = { currentScreen = "contacto"; scope.launch { drawerState.close() } },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Color(0xFFFFF0E0)
                    ),
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
                            text = when (currentScreen) {
                                "adoptar"    -> "Mascotas en Adopción"
                                "urgencias"  -> "Mapa de Urgencias"
                                "dar"        -> "Publicar Mascota"
                                "blog"       -> "Consejos y Blog"
                                "nosotros"   -> "Nuestra Misión"
                                "donaciones" -> "Campañas de Donación"
                                else         -> "Contacto"
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
                        containerColor = naranjaPrincipal,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when (currentScreen) {
                    "adoptar"    -> AdoptarScreen(viewModel)
                    "urgencias"  -> UrgenciasScreen(viewModel = viewModel)
                    "dar"        -> DarAdopcionScreen(viewModel, onSaved = { currentScreen = "adoptar" })
                    "blog"       -> BlogScreen()
                    "nosotros"   -> NosotrosScreen()
                    "donaciones" -> DonacionesScreen()
                    "contacto"   -> ContactoScreen()
                }
            }
        }
    }
}