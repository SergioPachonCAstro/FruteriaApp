package com.example.fruteria

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import java.util.Locale
import android.widget.ImageView

class DetalleActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle)

        // Relacionamos los elementos del diseño con el código.
        val textoNombre = findViewById<TextView>(R.id.textoNombre)
        val textoPrecio = findViewById<TextView>(R.id.textoPrecio)
        val textoTipo = findViewById<TextView>(R.id.textoTipo)
        val botonVolver = findViewById<Button>(R.id.botonVolver)
        val imagenProducto = findViewById<ImageView>(R.id.imagenProducto)

        // Recibimos los datos enviados desde la pantalla principal.
        val nombre = intent.getStringExtra("NOMBRE_EXTRA") ?: ""
        val precio = intent.getDoubleExtra("PRECIO_EXTRA", 0.0)
        val tipo = intent.getStringExtra("TIPO_EXTRA") ?: ""

        // Mostramos los datos del producto.
        textoNombre.text = nombre
        textoPrecio.text = String.format(
            Locale.getDefault(),
            "%.2f €/kg",
            precio
        )
        textoTipo.text = tipo
        // Mostramos la imagen según el tipo de producto.
        if (tipo == "Fruta") {
            imagenProducto.setImageResource(R.drawable.fruta)
        } else {
            imagenProducto.setImageResource(R.drawable.verdura)
        }

        // Cerramos esta pantalla para regresar a la anterior.
        botonVolver.setOnClickListener {
            finish()
        }
    }
}