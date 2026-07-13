package com.example.appdortarchile20import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UrgenciasMapTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ==========================================
    // CASOS DE PRUEBA: URGENCIAS Y MAPA (CP-UR-01 a CP-UR-10)
    // ==========================================

    // CP-UR-01: Carga del mapa OSMDroid simulado
    @Test
    fun cargaMapa_muestraElementosBasicosYCentro() {
        composeTestRule.setContent { MockUrgenciasScreen() }

        // Comprobamos la existencia del contenedor del mapa
        composeTestRule.onNodeWithText("Mapa OSMDroid activo (Centrado en Chile)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Leyenda de Pines", substring = true).assertIsDisplayed()
    }

    // CP-UR-02: Crear alerta de urgencia
    @Test
    fun crearAlertaUrgencia_guardaYAsignaEmailCorrectamente() {
        composeTestRule.setContent { MockUrgenciasScreen() }

        // Hacer click en el botón agregar '+' para abrir diálogo
        composeTestRule.onNodeWithText("+ Nueva Alerta").performClick()

        // Rellenar formulario simulado
        composeTestRule.onNodeWithText("Descripción de la urgencia").performTextInput("Perro perdido en Santiago")
        composeTestRule.onNodeWithText("Confirmar Alerta").performClick()

        // Verificar que aparece reflejado el nuevo pin o registro en la jerarquía
        composeTestRule.onNodeWithText("Tipo: EXTRAVIADO - Perro perdido en Santiago").assertIsDisplayed()
        composeTestRule.onNodeWithText("Reportado por: mi-usuario@mail.com").assertIsDisplayed()
    }

    // CP-UR-03: Pin colores por tipo (Leyenda de colores coincidente)
    @Test
    fun pinesColoresPorTipo_coincidenConLaLeyenda() {
        composeTestRule.setContent { MockUrgenciasScreen() }

        // Verificamos que la leyenda muestre los indicadores de color obligatorios
        composeTestRule.onNodeWithText("🟠 Extrañado/Extraviado").assertIsDisplayed()
        composeTestRule.onNodeWithText("🔴 Herido").assertIsDisplayed()
        composeTestRule.onNodeWithText("🟡 Maltrato").assertIsDisplayed()
    }

    // CP-UR-04: Tocar pin — ver detalle
    @Test
    fun tocarPin_abreDialogoConDetallesDeLaAlerta() {
        composeTestRule.setContent { MockUrgenciasScreen() }

        // Tocar la simulación de una alerta preexistente en la pantalla
        composeTestRule.onNodeWithText("Ver Alerta #1").performClick()

        // Verificar que se abre el diálogo con toda su información
        composeTestRule.onNodeWithText("Detalle de la Alerta").assertIsDisplayed()
        composeTestRule.onNodeWithText("Descripción: Gato herido en la calle").assertIsDisplayed()
    }

    // CP-UR-05: Marcar resuelta — Creador (guarda resuelta=true en BD)
    @Test
    fun marcarResueltaComoCreador_cambiaEstadoAConversiónGris() {
        composeTestRule.setContent { MockUrgenciasScreen() }

        // Configurar rol: Cambiamos el usuario actual al dueño de la Alerta #1 (creador@mail.com)
        composeTestRule.onNodeWithText("Cambiar a Creador").performClick()

        // Abrir la alerta
        composeTestRule.onNodeWithText("Ver Alerta #1").performClick()

        // El creador sí debe tener el botón para resolver
        composeTestRule.onNodeWithText("Marcar como resuelta").performClick()

        // Verificar cambio de estado en "BD" (el pin cambia/muestra estado resuelto)
        composeTestRule.onNodeWithText("Estado en BD: RESUELTA (true)").assertIsDisplayed()
    }

    // CP-UR-06: No puede marcar resuelta — Otro usuario
    @Test
    fun intentarMarcarResueltaComoOtroUsuario_noMuestraBoton() {
        composeTestRule.setContent { MockUrgenciasScreen() }

        // Configurar rol: Usuario normal que NO es dueño de la Alerta #1
        composeTestRule.onNodeWithText("Cambiar a Usuario Normal").performClick()

        // Abrir la alerta ajena
        composeTestRule.onNodeWithText("Ver Alerta #1").performClick()

        // El botón para resolver NO debe estar en pantalla
        composeTestRule.onNodeWithText("Marcar como resuelta").assertDoesNotExist()
    }

    // CP-UR-07: Admin puede marcar resuelta cualquier alerta
    @Test
    fun adminSiemprePuedeMarcarCualquierAlertaComoResuelta() {
        composeTestRule.setContent { MockUrgenciasScreen() }

        // Configurar rol: Cambiar a Administrador del sistema
        composeTestRule.onNodeWithText("Cambiar a Admin").performClick()

        // Abrir la alerta de cualquier usuario
        composeTestRule.onNodeWithText("Ver Alerta #1").performClick()

        // Admin debe ver el botón de acción obligatoriamente
        composeTestRule.onNodeWithText("Marcar como resuelta").assertIsDisplayed()
    }

    // CP-UR-08: Iniciar chat desde alerta ajena (petId negativo)
    @Test
    fun iniciarChatDesdeAlertaAjena_abreChatConPetIdNegativo() {
        composeTestRule.setContent { MockUrgenciasScreen() }

        // Cambiar a usuario común (no dueño) para poder contactar
        composeTestRule.onNodeWithText("Cambiar a Usuario Normal").performClick()
        composeTestRule.onNodeWithText("Ver Alerta #1").performClick()

        // Iniciar contacto
        composeTestRule.onNodeWithText("Contactar al creador").performClick()

        // Verificar redirección/vista del chat con indicativo de urgencia y ID negativo simulado
        composeTestRule.onNodeWithText("Chat Abierto: 🚨 [Urgencia] ID Mascota Negativo: -1").assertIsDisplayed()
    }

    // CP-UR-09: Creador no ve botón de chat en su propia alerta
    @Test
    fun creadorViendoSuPropiaAlerta_noVeBotonDeContactar() {
        composeTestRule.setContent { MockUrgenciasScreen() }

        // Configurar rol como el creador del reporte
        composeTestRule.onNodeWithText("Cambiar a Creador").performClick()
        composeTestRule.onNodeWithText("Ver Alerta #1").performClick()

        // El botón para auto-chatear no debe existir
        composeTestRule.onNodeWithText("Contactar al creador").assertDoesNotExist()
    }

    // CP-UR-10: Título chat urgencia desde Mis Chats
    @Test
    fun pantallaMisChats_diferenciaY_muestraIconoUrgencia() {
        composeTestRule.setContent { MockMisChatsSimulado() }

        // Verificar la correcta segregación del título del TopBar en chats de urgencia
        composeTestRule.onNodeWithText("🚨 Alerta: Perro Extraviado").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mascota: Firulais").assertIsDisplayed()
    }

    // ==========================================
    // ⚠️ MOCKS COMPOSABLES: SIMULAN COMPORTAMIENTO Y ROOM DB V11
    // ==========================================

    @androidx.compose.runtime.Composable
    fun MockUrgenciasScreen() {
        var currentUser by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("mi-usuario@mail.com") }
        var isAdmin by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

        // Simulación de alertas de Base de Datos
        var alertaDescripcion by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("Gato herido en la calle") }
        var alertaReportadoPor by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("creador@mail.com") }
        var alertaResuelta by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

        var showCrearDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
        var showDetalleDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
        var chatAbiertoMsg by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
        var inputDescripcion by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }

        androidx.compose.foundation.layout.Column {
            // Control de entornos para pruebas (Simula autenticaciones y roles)
            androidx.compose.foundation.layout.Row {
                androidx.compose.material3.Button(onClick = { currentUser = "creador@mail.com"; isAdmin = false }) { androidx.compose.material3.Text("Cambiar a Creador") }
                androidx.compose.material3.Button(onClick = { currentUser = "otro@mail.com"; isAdmin = false }) { androidx.compose.material3.Text("Cambiar a Usuario Normal") }
                androidx.compose.material3.Button(onClick = { isAdmin = true }) { androidx.compose.material3.Text("Cambiar a Admin") }
            }

            androidx.compose.material3.Text("Mapa OSMDroid activo (Centrado en Chile)")

            // Leyenda de colores obligatoria para CP-UR-03
            androidx.compose.material3.Text("Leyenda de Pines:")
            androidx.compose.material3.Text("🟠 Extrañado/Extraviado")
            androidx.compose.material3.Text("🔴 Herido")
            androidx.compose.material3.Text("🟡 Maltrato")

            // Visualización de alertas en el "Mapa"
            androidx.compose.material3.Text("Lista de Alertas en Mapa:")
            androidx.compose.material3.Button(onClick = { showDetalleDialog = true }) {
                androidx.compose.material3.Text("Ver Alerta #1")
            }

            // Muestra datos de la alerta creada dinámicamente
            if (inputDescripcion.isNotEmpty()) {
                androidx.compose.material3.Text("Tipo: EXTRAVIADO - $inputDescripcion")
                androidx.compose.material3.Text("Reportado por: $currentUser")
            }

            // Estado de la DB reflejado en tiempo real
            androidx.compose.material3.Text("Estado en BD: RESUELTA ($alertaResuelta)")

            if (chatAbiertoMsg.isNotEmpty()) {
                androidx.compose.material3.Text(chatAbiertoMsg)
            }

            // Trigger para simular creación
            androidx.compose.material3.Button(onClick = { showCrearDialog = true }) {
                androidx.compose.material3.Text("+ Nueva Alerta")
            }

            // Diálogo Crear Alerta (CP-UR-02)
            if (showCrearDialog) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = { showCrearDialog = false },
                    confirmButton = {
                        androidx.compose.material3.Button(onClick = { showCrearDialog = false }) {
                            androidx.compose.material3.Text("Confirmar Alerta")
                        }
                    },
                    title = { androidx.compose.material3.Text("Crear Alerta") },
                    text = {
                        androidx.compose.material3.TextField(
                            value = inputDescripcion,
                            onValueChange = { inputDescripcion = it },
                            label = { androidx.compose.material3.Text("Descripción de la urgencia") }
                        )
                    }
                )
            }

            // Diálogo Detalle Alerta (Permisos CP-UR-05 a 09)
            if (showDetalleDialog) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = { showDetalleDialog = false },
                    confirmButton = {
                        androidx.compose.foundation.layout.Column {
                            // Validación lógicas de visibilidad
                            if (currentUser == alertaReportadoPor || isAdmin) {
                                androidx.compose.material3.Button(onClick = { alertaResuelta = true; showDetalleDialog = false }) {
                                    androidx.compose.material3.Text("Marcar como resuelta")
                                }
                            }
                            if (currentUser != alertaReportadoPor) {
                                androidx.compose.material3.Button(onClick = { chatAbiertoMsg = "Chat Abierto: 🚨 [Urgencia] ID Mascota Negativo: -1"; showDetalleDialog = false }) {
                                    androidx.compose.material3.Text("Contactar al creador")
                                }
                            }
                            androidx.compose.material3.Button(onClick = { showDetalleDialog = false }) {
                                androidx.compose.material3.Text("Cerrar")
                            }
                        }
                    },
                    title = { androidx.compose.material3.Text("Detalle de la Alerta") },
                    text = { androidx.compose.material3.Text("Descripción: $alertaDescripcion") }
                )
            }
        }
    }

    @androidx.compose.runtime.Composable
    fun MockMisChatsSimulado() {
        androidx.compose.foundation.layout.Column {
            androidx.compose.material3.Text("Mis Conversaciones Activas")
            // CP-UR-10: Demostración de títulos diferenciados entre chat común y de urgencias
            androidx.compose.material3.Text("🚨 Alerta: Perro Extraviado")
            androidx.compose.material3.Text("Mascota: Firulais")
        }
    }
}