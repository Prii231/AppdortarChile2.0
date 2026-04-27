package com.example.appdortarchile20

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.appdortarchile20.ui.screens.*
import com.example.appdortarchile20.ui.theme.AppdortarChile20Theme
import com.example.appdortarchile20.ui.viewmodel.PetViewModel
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- CONFIGURACIÓN PARA OPENSTREETMAP (PLAN B) ---
        // Esto es vital para que el mapa cargue las imágenes (tiles) correctamente.
        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )
        // Identifica tu app ante los servidores de mapas
        Configuration.getInstance().userAgentValue = packageName

        setContent {
            AppdortarChile20Theme {
                val viewModel: PetViewModel = viewModel()

                // Insertamos datos de ejemplo la primera vez
                LaunchedEffect(Unit) {
                    viewModel.insertDummyData()
                }

                AppNavigation(viewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: PetViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // 1. Pantalla de Login
        composable("login") {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate("splash") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate("register")
                }
            )
        }

        // 2. Pantalla de Registro
        composable("register") {
            RegisterScreen(
                viewModel = viewModel,
                onRegistered = {
                    navController.navigate("splash") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        // 3. Pantalla Splash
        composable("splash") {
            SplashScreen(
                onTimeout = {
                    navController.navigate("main_content") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // 4. Contenedor Principal
        composable("main_content") {
            val currentUser by viewModel.currentUser.collectAsState()
            val navBackStack by navController.currentBackStackEntryAsState()

            // Si el usuario pierde la sesión estando en main_content, vuelve al login
            LaunchedEffect(currentUser) {
                if (currentUser == null &&
                    navBackStack?.destination?.route == "main_content") {
                    navController.navigate("login") {
                        popUpTo("main_content") { inclusive = true }
                    }
                }
            }

            MainDrawerScreen(
                viewModel = viewModel,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("main_content") { inclusive = true }
                    }
                }
            )
        }
    }
}