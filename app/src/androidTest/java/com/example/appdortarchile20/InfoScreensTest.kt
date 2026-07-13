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
class InfoScreensTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ==========================================
    // CASOS DE PRUEBA: INFORMACIÓN Y CONTACTO (CP-IN-01 a CP-IN-04)
    // ==========================================

    // 🛠️ CP-IN-01: Blog expandible (Tip Técnico)
    @Test
    fun expandirBlog_muestraContenidoOcultoYCambiaTextoBoton() {
        composeTestRule.setContent { MockInfoScreen() }

        // 1. Estado inicial: El contenido extendido no debe existir y el botón debe decir "Leer más"
        composeTestRule.onNodeWithText("Texto completo del artículo del blog sobre cuidados...").assertDoesNotExist()
        composeTestRule.onNodeWithText("Leer más").assertIsDisplayed()

        // 2. Acción: Click en el expansor
        composeTestRule.onNodeWithText("Leer más").performClick()

        // 3. Validación: El texto oculto se renderiza y el botón muta a "Ver menos"
        composeTestRule.onNodeWithText("Texto completo del artículo del blog sobre cuidados...").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ver menos").assertIsDisplayed()
    }

    // CP-IN-02: Visualización de Enlaces de Contacto/Redes
    @Test
    fun seccionContacto_muestraCorreoYRedesSociales() {
        composeTestRule.setContent { MockInfoScreen() }

        // Comprobar la presencia de las vías de contacto oficiales
        composeTestRule.onNodeWithText("Correo: contacto@appdoptar.cl").assertIsDisplayed()
        composeTestRule.onNodeWithText("Instagram: @appdoptar").assertIsDisplayed()
    }

    // CP-IN-03: Sección FAQ / Acerca de la app
    @Test
    fun seccionAcercaDe_muestraVersionYPreguntasFrecuentes() {
        composeTestRule.setContent { MockInfoScreen() }

        // Comprobar la información legal y de ayuda
        composeTestRule.onNodeWithText("Appdoptar Chile - Versión 2.0").assertIsDisplayed()
        composeTestRule.onNodeWithText("FAQ: ¿Cómo adoptar una mascota?").assertIsDisplayed()
    }

    // CP-IN-04: Funcionamiento del Formulario de Contacto
    @Test
    fun enviarFormularioContacto_procesaY_MuestraMensajeDeExito() {
        composeTestRule.setContent { MockInfoScreen() }

        // Rellenar el input de contacto
        composeTestRule.onNodeWithText("Escribe tu consulta...").performTextInput("Necesito ayuda para registrar mi refugio")

        // Hacer click en enviar
        composeTestRule.onNodeWithText("Enviar Mensaje").performClick()

        // Verificar el feedback visual para el usuario
        composeTestRule.onNodeWithText("¡Mensaje enviado correctamente!").assertIsDisplayed()
    }

    // ==========================================
    // ⚠️ MOCKS COMPOSABLES
    // ==========================================

    @androidx.compose.runtime.Composable
    fun MockInfoScreen() {
        // Estado para la expansión del blog (CP-IN-01)
        var blogExpandido by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

        // Estados para el formulario (CP-IN-04)
        var consultaInput by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
        var formularioEnviado by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

        androidx.compose.foundation.layout.Column {

            // --- SECCIÓN: ACERCA DE Y CONTACTO (CP-IN-02 y CP-IN-03) ---
            androidx.compose.material3.Text("Appdoptar Chile - Versión 2.0")
            androidx.compose.material3.Text("FAQ: ¿Cómo adoptar una mascota?")
            androidx.compose.material3.Text("Correo: contacto@appdoptar.cl")
            androidx.compose.material3.Text("Instagram: @appdoptar")

            androidx.compose.material3.HorizontalDivider()

            // --- SECCIÓN: BLOG EDUCATIVO (CP-IN-01) ---
            androidx.compose.material3.Text("Blog: Cuidados básicos para tu nuevo perrito")

            // Simulación de AnimatedVisibility
            if (blogExpandido) {
                androidx.compose.material3.Text("Texto completo del artículo del blog sobre cuidados...")
            }

            androidx.compose.material3.Button(onClick = { blogExpandido = !blogExpandido }) {
                androidx.compose.material3.Text(if (blogExpandido) "Ver menos" else "Leer más")
            }

            androidx.compose.material3.HorizontalDivider()

            // --- SECCIÓN: FORMULARIO DE CONTACTO (CP-IN-04) ---
            androidx.compose.material3.Text("Contacta al Soporte")

            if (formularioEnviado) {
                androidx.compose.material3.Text("¡Mensaje enviado correctamente!")
            } else {
                androidx.compose.material3.TextField(
                    value = consultaInput,
                    onValueChange = { consultaInput = it },
                    label = { androidx.compose.material3.Text("Escribe tu consulta...") }
                )
                androidx.compose.material3.Button(onClick = {
                    if (consultaInput.isNotEmpty()) {
                        formularioEnviado = true
                    }
                }) {
                    androidx.compose.material3.Text("Enviar Mensaje")
                }
            }
        }
    }
}