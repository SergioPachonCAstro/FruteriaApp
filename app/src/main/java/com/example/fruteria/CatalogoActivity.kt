package com.example.fruteria

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class CatalogoActivity : ComponentActivity() {

    private lateinit var adapter: ProductoAdapter
    private lateinit var textoContador: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalogo)

        val listaProductos = findViewById<RecyclerView>(R.id.listaProductos)
        textoContador = findViewById(R.id.textoContador)
        val botonVolver = findViewById<Button>(R.id.botonVolver)
        val botonVaciar = findViewById<Button>(R.id.botonVaciar)

        // Muestra los productos uno debajo de otro.
        listaProductos.layoutManager = LinearLayoutManager(this)

        // Conecta el adaptador con la función para borrar un alimento individual al hacer clic largo.
        adapter = ProductoAdapter(emptyList()) { alimento ->
            AlertDialog.Builder(this)
                .setTitle("Eliminar producto")
                .setMessage("¿Quieres eliminar \"${alimento.nombre}\"?")
                .setPositiveButton("Eliminar") { _, _ ->
                    val dao = AppDatabase.obtener(this).alimentoDao()
                    lifecycleScope.launch {
                        dao.borrar(alimento)
                        cargarProductos()
                        Toast.makeText(
                            this@CatalogoActivity,
                            "Producto eliminado: ${alimento.nombre}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
        listaProductos.adapter = adapter

        // Botón para vaciar todo el catálogo.
        botonVaciar.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Vaciar catálogo")
                .setMessage("¿Quieres borrar todos los productos?")
                .setPositiveButton("Vaciar") { _, _ ->
                    val dao = AppDatabase.obtener(this).alimentoDao()
                    lifecycleScope.launch {
                        dao.borrarTodos()
                        cargarProductos()
                        Toast.makeText(
                            this@CatalogoActivity,
                            "Catálogo vaciado",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        botonVolver.setOnClickListener {
            finish()
        }
    }

    // Se ejecuta cada vez que la pantalla vuelve a ser visible.
    override fun onResume() {
        super.onResume()
        cargarProductos()
    }

    // Lee todos los alimentos de Room y actualiza la lista en pantalla.
    private fun cargarProductos() {
        val dao = AppDatabase.obtener(this).alimentoDao()
        lifecycleScope.launch {
            val alimentos = dao.obtenerTodas()
            adapter.actualizarLista(alimentos)
            textoContador.text = "Productos: ${alimentos.size}"
        }
    }
}