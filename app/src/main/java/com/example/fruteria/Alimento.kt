package com.example.fruteria

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Alimentos")
data class Alimento(
    val nombre: String,
    val precio: Double,
    val tipo: String,

    val latitud: Double? = null,
    val longitud: Double? = null,
    val fotoPath: String? = null,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
