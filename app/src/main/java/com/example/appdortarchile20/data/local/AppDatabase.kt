package com.example.appdortarchile20.data.local

import android.content.Context
import androidx.room.*
import com.example.appdortarchile20.data.model.User
import com.example.appdortarchile20.data.model.Pet
import com.example.appdortarchile20.data.model.UrgenciaReporte
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // --- USUARIOS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registerUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    // --- MASCOTAS ---
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPet(pet: Pet)

    @Query("SELECT * FROM pets")
    fun getAllPets(): Flow<List<Pet>>

    @Delete
    suspend fun deletePet(pet: Pet)

    @Query("DELETE FROM pets")
    suspend fun deleteAllPets()

    // --- URGENCIAS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReporte(reporte: UrgenciaReporte)

    @Delete
    suspend fun deleteReporte(reporte: UrgenciaReporte)

    @Query("SELECT * FROM urgencias ORDER BY horaReporte DESC")
    fun getAllReportes(): Flow<List<UrgenciaReporte>>
}

@Database(
    entities = [User::class, Pet::class, UrgenciaReporte::class],
    version = 4,
    exportSchema = false
)
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
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}