package com.example.appdortarchile20.data.local

import android.content.Context
import androidx.room.*
import com.example.appdortarchile20.data.model.User
import com.example.appdortarchile20.data.model.Pet
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // --- USUARIOS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registerUser(user: User)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    // --- MASCOTAS ---

    // Cambiado a IGNORE: Si el ID ya existe, no hará nada (evita duplicados)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPet(pet: Pet)

    @Query("SELECT * FROM pets")
    fun getAllPets(): Flow<List<Pet>>

    // Función extra: Útil para que el usuario pueda borrar su publicación
    @Delete
    suspend fun deletePet(pet: Pet)

    // Función extra: Limpiar toda la tabla (útil para pruebas)
    @Query("DELETE FROM pets")
    suspend fun deleteAllPets()
}

@Database(entities = [User::class, Pet::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "appdoptar_database"
                )
                    // Esto permite que si cambias algo en el modelo Pet o User más adelante,
                    // la app no se cierre (aunque borrará los datos antiguos)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}