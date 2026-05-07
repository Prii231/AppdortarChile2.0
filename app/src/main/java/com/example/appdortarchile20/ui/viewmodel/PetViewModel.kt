package com.example.appdortarchile20.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appdortarchile20.data.local.AppDatabase
import com.example.appdortarchile20.data.model.Evaluacion
import com.example.appdortarchile20.data.model.Mensaje
import com.example.appdortarchile20.data.model.Pet
import com.example.appdortarchile20.data.model.UrgenciaReporte
import com.example.appdortarchile20.data.model.User
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}

class PetViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).appDao()

    // Estado de Mascotas: Se actualiza automáticamente desde Room
    val allPets: StateFlow<List<Pet>> = dao.getAllPets()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Estado de Reportes de Urgencia
    val allReportes: StateFlow<List<UrgenciaReporte>> = dao.getAllReportes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Estado de Login
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    // Usuario autenticado actualmente
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            val user = dao.getUserByEmail(email)
            if (user != null && user.password == pass) {
                _currentUser.value = user
                _loginState.value = LoginState.Success(user)
            } else {
                _loginState.value = LoginState.Error("Correo o contraseña incorrectos")
            }
        }
    }

    fun register(user: User) {
        viewModelScope.launch {
            try {
                dao.registerUser(user)
                _currentUser.value = user
                _loginState.value = LoginState.Success(user)
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Error al registrar: ${e.message}")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _loginState.value = LoginState.Idle
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            dao.updateUser(user)
            _currentUser.value = user
        }
    }

    fun addPet(pet: Pet) {
        viewModelScope.launch {
            dao.addPet(pet)
        }
    }

    fun deletePet(pet: Pet) {
        viewModelScope.launch {
            dao.deletePet(pet)
        }
    }

    fun addReporte(reporte: UrgenciaReporte) {
        viewModelScope.launch {
            dao.insertReporte(reporte)
        }
    }

    fun deleteReporte(reporte: UrgenciaReporte) {
        viewModelScope.launch {
            dao.deleteReporte(reporte)
        }
    }

    fun marcarResuelta(id: Int) {
        viewModelScope.launch {
            dao.marcarResuelta(id)
        }
    }

    // Datos iniciales de ejemplo con REGIONES de Chile
    fun insertDummyData() {
        viewModelScope.launch {
            if (allPets.value.isEmpty()) {
                val dummyPets = listOf(
                    Pet(1, "Firulais", "Perro", "2 años", "Metropolitana", "Santiago", "https://placedog.net/500", true, true, "Es muy juguetón.", "Juan Pérez", "+5691234", "juan@example.com"),
                    Pet(2, "Mimi", "Gato", "6 meses", "Valparaíso", "Viña del Mar", "https://placekitten.com/500", true, false, "Gatita muy tranquila.", "María Soto", "+5698765", "maria@example.com"),
                    Pet(3, "Pipo", "Otro", "1 año", "Biobío", "Concepción", "https://picsum.photos/id/1025/500", false, false, "Conejito sociable.", "Andrés Jara", "+5695554", "andres@example.com")
                )
                dummyPets.forEach { dao.addPet(it) }
            }
        }
    }

    // --- CHAT ---
    fun getMensajesChat(petId: Int, email1: String, email2: String) =
        dao.getMensajesChat(petId, email1, email2)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun getMensajesUsuario(email: String) =
        dao.getMensajesUsuario(email)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val mensajesNoLeidos: StateFlow<Int> = _currentUser
        .flatMapLatest { user ->
            if (user != null) dao.getMensajesNoLeidos(user.email)
            else kotlinx.coroutines.flow.flowOf(0)
        }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    fun enviarMensaje(petId: Int, destinatarioEmail: String, texto: String) {
        val remitente = _currentUser.value?.email ?: return
        viewModelScope.launch {
            dao.insertMensaje(
                Mensaje(petId = petId, remitenteEmail = remitente,
                    destinatarioEmail = destinatarioEmail, texto = texto)
            )
        }
    }

    fun marcarComoLeidos(petId: Int) {
        val email = _currentUser.value?.email ?: return
        viewModelScope.launch { dao.marcarComoLeidos(petId, email) }
    }

    // --- EVALUACIONES ---
    fun getPromedioEstrellas(email: String) =
        dao.getPromedioEstrellas(email)
            .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun getEvaluacionesUsuario(email: String) =
        dao.getEvaluacionesUsuario(email)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun enviarEvaluacion(petId: Int, evaluadoEmail: String, estrellas: Int, comentario: String) {
        val evaluador = _currentUser.value?.email ?: return
        viewModelScope.launch {
            val existente = dao.getEvaluacionExistente(petId, evaluador)
            if (existente == null) {
                dao.insertEvaluacion(
                    Evaluacion(petId = petId, evaluadorEmail = evaluador,
                        evaluadoEmail = evaluadoEmail, estrellas = estrellas,
                        comentario = comentario)
                )
            }
        }
    }
}