package com.example.fruteria
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AlimentoDAO {

    // Guarda un alimento en la base de datos
    @Insert
    suspend fun insertar(alimento: Alimento)

    // Obtiene los alimentos, empezando por el último añadido
    @Query("SELECT * FROM Alimentos ORDER BY id DESC")
    suspend fun obtenerTodas(): List<Alimento>

    // Elimina el alimento indicado
    @Delete
    suspend fun borrar(alimento: Alimento)

    // Elimina todos los alimentos de la base de datos
    @Query("DELETE FROM Alimentos")
    suspend fun borrarTodos()
}