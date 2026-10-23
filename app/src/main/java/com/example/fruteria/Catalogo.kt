package com.example.fruteria

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object Catalogo {

    val productos = mutableListOf<Producto>()

    // Guarda la lista en el almacenamiento de la aplicación.
    fun guardar(context: Context) {
        val listaJSON = JSONArray()

        for (producto in productos) {
            val datos = JSONObject()

            datos.put("nombre", producto.nombre)
            datos.put("precio", producto.precio)
            datos.put("tipo", producto.tipo)

            listaJSON.put(datos)
        }

        val preferencias = context.getSharedPreferences(
            "fruteria",
            Context.MODE_PRIVATE
        )

        preferencias.edit()
            .putString("productos", listaJSON.toString())
            .apply()
    }

    // Recupera los productos guardados.
    fun cargar(context: Context) {
        val preferencias = context.getSharedPreferences(
            "fruteria",
            Context.MODE_PRIVATE
        )

        val texto = preferencias.getString("productos", "[]") ?: "[]"
        val listaJSON = JSONArray(texto)

        // Evita duplicados si volvemos a cargar la lista.
        productos.clear()

        for (i in 0 until listaJSON.length()) {
            val datos = listaJSON.getJSONObject(i)

            val producto = Producto(
                datos.getString("nombre"),
                datos.getDouble("precio"),
                datos.getString("tipo")
            )

            productos.add(producto)
        }
    }
}