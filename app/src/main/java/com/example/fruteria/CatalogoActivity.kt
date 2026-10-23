package com.example.fruteria

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.app.AlertDialog

class CatalogoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalogo)

        val listaProductos = findViewById<RecyclerView>(R.id.listaProductos)
        val textoContador = findViewById<TextView>(R.id.textoContador)
        val botonVolver = findViewById<Button>(R.id.botonVolver)
        val botonVaciar = findViewById<Button>(R.id.botonVaciar)

        // Muestra los productos uno debajo de otro.
        listaProductos.layoutManager = LinearLayoutManager(this)

        // Conecta la lista de productos con el adaptador.
        val adapter = ProductoAdapter(Catalogo.productos)
        listaProductos.adapter = adapter

        // Muestra cuántos productos hay guardados.
        textoContador.text = "Productos: ${Catalogo.productos.size}"

        botonVaciar.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Vaciar catálogo")
                .setMessage("¿Quieres borrar todos los productos?")
                .setPositiveButton("Vaciar") { _, _ ->

                    // Borramos la lista y guardamos el catálogo vacío.
                    Catalogo.productos.clear()
                    Catalogo.guardar(this)

                    // Actualizamos la lista visible y el contador.
                    adapter.notifyDataSetChanged()
                    textoContador.text = "Productos: 0"
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        botonVolver.setOnClickListener {
            finish()
        }
    }
}