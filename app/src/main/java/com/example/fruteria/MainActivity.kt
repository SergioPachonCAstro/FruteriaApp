package com.example.fruteria

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import android.content.Intent

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Recuperamos el catálogo al abrir la pantalla principal.
        Catalogo.cargar(this)

        // Relacionamos los elementos del diseño con el código.
        val campoNombre = findViewById<EditText>(R.id.campoNombre)
        val campoPrecio = findViewById<EditText>(R.id.campoPrecio)
        val grupoTipo = findViewById<RadioGroup>(R.id.grupoTipo)
        val botonAnadir = findViewById<Button>(R.id.botonAnadir)
        val botonCatalogo = findViewById<Button>(R.id.botonCatalogo)

// Abre la pantalla con los productos guardados.
        botonCatalogo.setOnClickListener {
            val intent = Intent(this, CatalogoActivity::class.java)
            startActivity(intent)
        }


        botonAnadir.setOnClickListener {

            // Recogemos lo que ha escrito el usuario.
            val nombre = campoNombre.text.toString().trim()
            val precioTexto = campoPrecio.text.toString().trim()

            // Aceptamos coma o punto y convertimos el precio a número.
            val precio = precioTexto.replace(",", ".").toDoubleOrNull()

            if (nombre.isEmpty()) {
                Toast.makeText(
                    this,
                    "Introduce el nombre del producto",
                    Toast.LENGTH_SHORT
                ).show()

            } else if (precio == null || !precio.isFinite() || precio <= 0) {
                Toast.makeText(
                    this,
                    "Introduce un precio válido mayor que cero",
                    Toast.LENGTH_SHORT
                ).show()

            } else if (grupoTipo.checkedRadioButtonId == -1) {
                Toast.makeText(
                    this,
                    "Selecciona fruta o verdura",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                // Comprobamos qué tipo ha seleccionado.
                val tipo: String

                if (grupoTipo.checkedRadioButtonId == R.id.opcionFruta) {
                    tipo = "Fruta"
                } else {
                    tipo = "Verdura"
                }

                // Creamos el producto y lo guardamos en la lista.
                val producto = Producto(nombre, precio, tipo)
                Catalogo.productos.add(producto)
                Catalogo.guardar(this)
                // Abrimos la ficha y enviamos los datos del producto.
                val intent = Intent(this, DetalleActivity::class.java)

                intent.putExtra("NOMBRE_EXTRA", nombre)
                intent.putExtra("PRECIO_EXTRA", precio)
                intent.putExtra("TIPO_EXTRA", tipo)

                startActivity(intent)

                Toast.makeText(
                    this,
                    "Producto añadido: $nombre",
                    Toast.LENGTH_SHORT
                ).show()

                // Limpiamos el formulario para añadir otro producto.
                campoNombre.text.clear()
                campoPrecio.text.clear()
                grupoTipo.clearCheck()
            }
        }
    }
}