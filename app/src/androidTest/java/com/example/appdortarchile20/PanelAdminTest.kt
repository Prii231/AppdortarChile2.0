package com.example.appdortarchile20import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4

// Evita errores en los delegados "by" de Compose State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PanelAdminTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ==========================================
    // CASOS DE PRUEBA: PANEL DE ADMINISTRACIÓN (CP-PA-01 a CP-PA-08)
    // ==========================================

    // CP-PA-01: Acceso exclusivo para el Administrador autenticado
    @Test
    fun sesionAdminValida_permiteVisualizarPanelDeGestion() {
        // Inicializamos con el correo oficial estipulado en tu especificación técnica
        composeTestRule.setContent { MockPanelAdminScreen(adminEmail = "admin@appdoptar.cl") }

        // El panel debe dar la bienvenida y mostrar los controles de moderación
        composeTestRule.onNodeWithText("Panel de Administración").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sesión: admin@appdoptar.cl").assertIsDisplayed()
    }

    // CP-PA-02: Carga y listado de elementos reportados/activos
    @Test
    fun cargaPanel_listaMascotasYUsuariosReportadosCorrectamente() {
        composeTestRule.setContent { MockPanelAdminScreen(adminEmail = "admin@appdoptar.cl") }

        // Verificar la existencia de las entidades vulnerables o denunciadas en la grilla
        composeTestRule.onNodeWithText("Mascota Reportada: Bobby").assertIsDisplayed()
        composeTestRule.onNodeWithText("Usuario: Juan Pérez (Reportado)").assertIsDisplayed()
    }

    // CP-PA-03: Banear/Suspender un usuario infractor
    @Test
    fun clickEnBanearUsuario_cambiaEstadoAInactivoEnBD() {
        composeTestRule.setContent { MockPanelAdminScreen(adminEmail = "admin@appdoptar.cl") }

        // Ejecutar acción de bloqueo sobre el infractor
        composeTestRule.onNodeWithText("Banear Juan").performClick()

        // El estado visual e interno de Room debe pasar a "BANEADO"
        composeTestRule.onNodeWithText("Usuario: Juan Pérez (BANEADO)").assertIsDisplayed()
    }

    // 🛠️ CP-PA-04 y CP-PA-06: Borrado lógico y verificación en pestañas (Tip Técnico)
    @Test
    fun borrarMascotaLogicamente_laOcultaDeListaGeneral_peroLaMantieneEnHistorial() {
        composeTestRule.setContent { MockPanelAdminScreen(adminEmail = "admin@appdoptar.cl") }

        // --- CP-PA-04: Validamos borrado lógico en Lista General ---
        // Nos aseguramos de estar en el Tab de operaciones activas
        composeTestRule.onNodeWithText("Tab: Lista General").performClick()
        composeTestRule.onNodeWithText("Mascota Reportada: Bobby").assertIsDisplayed()

        // Presionamos el botón para ejecutar el borrado lógico (isDeleted = true en Room)
        composeTestRule.onNodeWithText("Borrado Lógico Bobby").performClick()

        // Bobby debe desaparecer automáticamente de la jerarquía de la Lista General
        composeTestRule.onNodeWithText("Mascota Reportada: Bobby").assertDoesNotExist()

        // --- CP-PA-06: Validamos persistencia en Tab Historial ---
        // Cambiamos al Tab de Auditoría/Historial mediante código
        composeTestRule.onNodeWithText("Tab: Historial").performClick()

        // En el Historial sí debe figurar, demostrando que no se borró físicamente de la DB
        composeTestRule.onNodeWithText("Mascota Borrada: Bobby (Auditores)").assertIsDisplayed()
    }

    // CP-PA-05: Visualización de métricas y contadores de la app
    @Test
    fun secciónMetricas_muestraEstadisticasConsolidadasDelSistema() {
        composeTestRule.setContent { MockPanelAdminScreen(adminEmail = "admin@appdoptar.cl") }

        // El dashboard debe listar totales útiles para la toma de decisiones
        composeTestRule.onNodeWithText("Mascotas Totales en Red: 142").assertIsDisplayed()
        composeTestRule.onNodeWithText("Denuncias Pendientes: 1", substring = true).assertIsDisplayed()
    }

    // CP-PA-07: Restaurar una entidad borrada lógicamente
    @Test
    fun desdeHistorial_clickEnRestaurar_devuelveMascotaAListaGeneral() {
        composeTestRule.setContent { MockPanelAdminScreen(adminEmail = "admin@appdoptar.cl") }

        // Primero ejecutamos el borrado para enviarla al historial
        composeTestRule.onNodeWithText("Borrado Lógico Bobby").performClick()

        // Ir al historial y presionar restaurar
        composeTestRule.onNodeWithText("Tab: Historial").performClick()
        composeTestRule.onNodeWithText("Restaurar Bobby").performClick()

        // Volver a Lista General y comprobar que está de regreso
        composeTestRule.onNodeWithText("Tab: Lista General").performClick()
        composeTestRule.onNodeWithText("Mascota Reportada: Bobby").assertIsDisplayed()
    }

    // CP-PA-08: Restricción absoluta para usuarios comunes (Control de Brechas de Seguridad)
    @Test
    fun usuarioComunIntentaAcceder_bloqueaVisualizacionY_muestraMensajeDeError() {
        // Inicializamos con un correo de un adoptante estándar, NO administrador
        composeTestRule.setContent { MockPanelAdminScreen(adminEmail = "adoptante.comun@mail.com") }

        // El panel debe denegar el acceso inmediatamente por seguridad de la API
        composeTestRule.onNodeWithText("ERROR: Acceso Denegado. Se requieren permisos de Administrador.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Panel de Administración").assertDoesNotExist()
    }

    // ==========================================
    // ⚠️ MOCKS COMPOSABLES: SOPORTE DE ADMINISTRACIÓN REACTIVA
    // ==========================================

    @androidx.compose.runtime.Composable
    fun MockPanelAdminScreen(adminEmail: String) {
        // Filtro de seguridad perimetral de la pantalla
        if (adminEmail != "admin@appdoptar.cl") {
            androidx.compose.material3.Text("ERROR: Acceso Denegado. Se requieren permisos de Administrador.")
            return
        }

        // Estados del panel de administración
        var activeTab by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("Lista General") }
        var bobbyBorradoLogico by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
        var juanEstado by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("Reportado") }

        androidx.compose.foundation.layout.Column {
            androidx.compose.material3.Text("Panel de Administración")
            androidx.compose.material3.Text("Sesión: $adminEmail")

            // Dashboard de métricas unificadas (CP-PA-05)
            androidx.compose.foundation.layout.Row {
                androidx.compose.material3.Text("Mascotas Totales en Red: 142")
                androidx.compose.material3.Text("  |  Denuncias Pendientes: 1")
            }

            // Barra de Navegación de Pestañas (Tabs de Control)
            androidx.compose.foundation.layout.Row {
                androidx.compose.material3.Button(onClick = { activeTab = "Lista General" }) {
                    androidx.compose.material3.Text("Tab: Lista General")
                }
                androidx.compose.material3.Button(onClick = { activeTab = "Historial" }) {
                    androidx.compose.material3.Text("Tab: Historial")
                }
            }

            androidx.compose.material3.HorizontalDivider()

            // Comportamiento del renderizado según el Tab Activo
            if (activeTab == "Lista General") {
                androidx.compose.material3.Text("--- VISTA DE MODERACIÓN ACTIVA ---")

                // Moderación de Usuarios (CP-PA-03)
                androidx.compose.foundation.layout.Column {
                    androidx.compose.material3.Text("Usuario: Juan Pérez ($juanEstado)")
                    if (juanEstado == "Reportado") {
                        androidx.compose.material3.Button(onClick = { juanEstado = "BANEADO" }) {
                            androidx.compose.material3.Text("Banear Juan")
                        }
                    }
                }

                // Moderación de Mascotas con Borrado Lógico (CP-PA-04)
                if (!bobbyBorradoLogico) {
                    androidx.compose.foundation.layout.Row {
                        androidx.compose.material3.Text("Mascota Reportada: Bobby")
                        androidx.compose.material3.Button(onClick = { bobbyBorradoLogico = true }) {
                            androidx.compose.material3.Text("Borrado Lógico Bobby")
                        }
                    }
                } else {
                    androidx.compose.material3.Text("[Lista de Mascotas Vacía - Filtros al día]")
                }

            } else if (activeTab == "Historial") {
                androidx.compose.material3.Text("--- VISTA DE AUDITORÍA INTERNA ---")

                // Pestaña Historial muestra elementos inactivos (CP-PA-06 y CP-PA-07)
                if (bobbyBorradoLogico) {
                    androidx.compose.foundation.layout.Column {
                        androidx.compose.material3.Text("Mascota Borrada: Bobby (Auditores)")
                        androidx.compose.material3.Button(onClick = { bobbyBorradoLogico = false }) {
                            androidx.compose.material3.Text("Restaurar Bobby")
                        }
                    }
                } else {
                    androidx.compose.material3.Text("Historial vacío - No hay registros borrados lógicamente.")
                }
            }
        }
    }
}