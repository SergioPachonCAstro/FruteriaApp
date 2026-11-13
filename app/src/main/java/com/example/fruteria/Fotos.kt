package com.example.fruteria

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object Fotos {

    // Prepara un archivo vacío en la carpeta de la app donde se guardará la foto
    fun crearArchivo(context: Context): File {
        val carpetaFotos = File(context.filesDir, "fotos")
        if (!carpetaFotos.exists()) {
            carpetaFotos.mkdirs()
        }
        val nombreArchivo = "foto_${System.currentTimeMillis()}.jpg"
        return File(carpetaFotos, nombreArchivo)
    }

    // Convierte el archivo en una Uri segura accesible por la app de la cámara
    fun uriDe(context: Context, archivo: File): Uri {
        val authority = "${context.packageName}.fileprovider"
        return FileProvider.getUriForFile(context, authority, archivo)
    }

    // Copia una imagen seleccionada de la galería a un archivo propio de la app
    fun copiarDesdeUri(context: Context, uri: Uri): String? {
        return try {
            val archivo = crearArchivo(context)
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                archivo.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            archivo.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    // Carga un Bitmap a partir de la ruta del archivo guardada
    fun cargar(ruta: String?): Bitmap? {
        if (ruta.isNullOrEmpty()) return null
        val archivo = File(ruta)
        if (!archivo.exists()) return null
        return BitmapFactory.decodeFile(ruta)
    }
}
