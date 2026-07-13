package com.example.appdortarchile20

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RoomDatabaseV11Test {

    private lateinit var db: TestDatabaseV11
    private lateinit var dao: TestDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // 🛠️ Construimos la BD en memoria temporal y permitimos consultas en el hilo principal
        db = Room.inMemoryDatabaseBuilder(
            context, TestDatabaseV11::class.java
        )
            .allowMainThreadQueries() // 🟢 SOLUCIÓN: Permite la ejecución sincrónica en pruebas
            .build()

        dao = db.testDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        // 🟢 Solo intentamos cerrarla si realmente se logró inicializar
        if (::db.isInitialized) {
            db.close()
        }
    }

    // ==========================================
    // CASOS DE PRUEBA: BASE DE DATOS V11 (CP-BD-01 a CP-BD-06)
    // ==========================================

    // 🛠️ CP-BD-01: Unicidad de Correos Electrónicos (Tip Técnico)
    @Test
    fun insertarDosUsuariosConMismoEmail_lanzaExcepcionDeRestriccion() {
        val usuario1 = UsuarioTest(email = "test@appdoptar.cl", nombre = "Juan")
        val usuario2 = UsuarioTest(email = "test@appdoptar.cl", nombre = "Pedro") // Mismo email

        dao.insertUsuario(usuario1)

        // Validamos que Room rechace el segundo insert por la regla de Unique Index
        assertThrows(SQLiteConstraintException::class.java) {
            dao.insertUsuario(usuario2)
        }
    }

    // CP-BD-02: Llaves Foráneas (FK) - Mascota sin dueño válido
    @Test
    fun insertarMascotaConDuenoInexistente_lanzaExcepcionPorForeignKey() {
        val mascotaHuerfana = MascotaTest(duenoId = 999, nombre = "Firulais") // Dueño 999 no existe

        // Validamos que Room proteja la integridad referencial
        assertThrows(SQLiteConstraintException::class.java) {
            dao.insertMascota(mascotaHuerfana)
        }
    }

    // CP-BD-03: Borrado en Cascada (Delete Cascade)
    @Test
    fun borrarUsuario_eliminaAutomaticamenteSusMascotasEnCascada() {
        // 1. Insertar usuario y recuperar su ID
        val userId = dao.insertUsuario(UsuarioTest(email = "borrar@mail.com", nombre = "Ana")).toInt()

        // 2. Insertar mascota asociada a ese ID
        dao.insertMascota(MascotaTest(duenoId = userId, nombre = "Gatito"))

        // 3. Borramos al usuario
        dao.deleteUsuarioById(userId)

        // 4. Verificamos que la mascota desapareció automáticamente
        val mascotaEnBd = dao.getMascotaByDuenoId(userId)
        assertNull("La mascota debió borrarse en cascada", mascotaEnBd)
    }

    // 🛠️ CP-BD-04: IDs Negativos para Urgencias (Tip Técnico)
    @Test
    fun insertarChatUrgenciaConIdNegativo_guardaYRecuperaCorrectamente() {
        // Insertamos un dueño válido primero
        val userId = dao.insertUsuario(UsuarioTest(email = "urgencia@mail.com", nombre = "Carlos")).toInt()

        // Creamos una urgencia con ID negativo tal como lo especifica tu arquitectura V11
        val alertaUrgencia = MascotaTest(
            duenoId = userId,
            nombre = "Perrito Atropellado",
            isUrgencia = true,
            petIdNegativo = -15
        )
        dao.insertMascota(alertaUrgencia)

        // Comprobamos la recuperación mediante ese ID negativo
        val recuperado = dao.getUrgenciaByNegativeId(-15)

        assertEquals(-15, recuperado?.petIdNegativo)
        assertEquals(true, recuperado?.isUrgencia)
    }

    // CP-BD-05: Lectura y Escritura Básica (Integridad de Datos)
    @Test
    fun insertarUsuario_leeSusDatosCorrectamente() {
        val user = UsuarioTest(email = "integro@mail.com", nombre = "María")
        val insertedId = dao.insertUsuario(user).toInt()

        val recuperado = dao.getUsuario(insertedId)

        assertEquals("integro@mail.com", recuperado?.email)
        assertEquals("María", recuperado?.nombre)
    }

    // CP-BD-06: Estabilidad de la versión V11 (Campos booleanos)
    @Test
    fun actualizarEstadoResuelta_mantieneConsistenciaEnElRegistro() {
        val userId = dao.insertUsuario(UsuarioTest(email = "update@mail.com", nombre = "Luis")).toInt()
        val mascotaId = dao.insertMascota(MascotaTest(duenoId = userId, nombre = "Bobby", isUrgencia = true)).toInt()

        // Simulamos que el admin marcó la urgencia como resuelta
        dao.marcarComoResuelta(mascotaId)

        val recuperado = dao.getMascotaById(mascotaId)
        assertEquals("El estado no se actualizó correctamente", true, recuperado?.resuelta)
    }
}

// ==========================================
// ⚠️ COMPONENTES ROOM DE SOPORTE V11
// ==========================================

@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["email"], unique = true)] // CP-BD-01
)
data class UsuarioTest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val nombre: String
)

@Entity(
    tableName = "mascotas",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioTest::class,
            parentColumns = ["id"],
            childColumns = ["duenoId"],
            onDelete = ForeignKey.CASCADE // CP-BD-03
        )
    ]
)
data class MascotaTest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val duenoId: Int,
    val nombre: String,
    val isUrgencia: Boolean = false,
    val resuelta: Boolean = false, // CP-BD-06
    val petIdNegativo: Int? = null // CP-BD-04
)

@Dao
interface TestDao {
    @Insert
    fun insertUsuario(usuario: UsuarioTest): Long

    @Insert
    fun insertMascota(mascota: MascotaTest): Long

    @Query("SELECT * FROM usuarios WHERE id = :id")
    fun getUsuario(id: Int): UsuarioTest?

    @Query("SELECT * FROM mascotas WHERE duenoId = :duenoId LIMIT 1")
    fun getMascotaByDuenoId(duenoId: Int): MascotaTest?

    @Query("SELECT * FROM mascotas WHERE id = :id")
    fun getMascotaById(id: Int): MascotaTest?

    @Query("SELECT * FROM mascotas WHERE petIdNegativo = :id")
    fun getUrgenciaByNegativeId(id: Int): MascotaTest?

    @Query("DELETE FROM usuarios WHERE id = :id")
    fun deleteUsuarioById(id: Int)

    @Query("UPDATE mascotas SET resuelta = 1 WHERE id = :id")
    fun marcarComoResuelta(id: Int)
}

@Database(entities = [UsuarioTest::class, MascotaTest::class], version = 11, exportSchema = false)
abstract class TestDatabaseV11 : RoomDatabase() {
    abstract fun testDao(): TestDao
}