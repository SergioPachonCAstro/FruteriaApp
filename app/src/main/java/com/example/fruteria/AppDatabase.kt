package com.example.fruteria

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Alimento::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun alimentoDao(): AlimentoDAO

    companion object {

        @Volatile
        private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase {
            return instancia ?: synchronized(this) {
                val nueva = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fruteria_base_de_datos"
                ).build()

                instancia = nueva
                nueva
            }
        }
    }
}