package com.example.fruteria

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import java.util.Locale

class DetalleActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle)

        // Relacionamos los elementos del diseño con el código.
        val textoNombre = findViewById<TextView>(R.id.textoNombre)
        val textoPrecio = findViewById<TextView>(R.id.textoPrecio)
        val textoTipo = findViewById<TextView>(R.id.textoTipo)
        val textoUbicacion = findViewById<TextView>(R.id.textoUbicacion)
        val botonVolver = findViewById<Button>(R.id.botonVolver)
        val imagenProducto = findViewById<ImageView>(R.id.imagenProducto)

        // Recibimos los datos enviados desde la pantalla principal.
        val nombre = intent.getStringExtra("NOMBRE_EXTRA") ?: ""
        val precio = intent.getDoubleExtra("PRECIO_EXTRA", 0.0)
        val tipo = intent.getStringExtra("TIPO_EXTRA") ?: ""
        val fotoPath = intent.getStringExtra("FOTO_EXTRA")

        val tieneLatitud = intent.hasExtra("LATITUD_EXTRA")
        val tieneLongitud = intent.hasExtra("LONGITUD_EXTRA")

        // Mostramos los datos del producto.
        textoNombre.text = nombre
        textoPrecio.text = String.format(
            Locale.getDefault(),
            "%.2f €/kg",
            precio
        )
        textoTipo.text = tipo

        // Mostramos la foto real si existe, o la imagen por defecto según el tipo
        val bitmapCustom = Fotos.cargar(fotoPath)
        if (bitmapCustom != null) {
            imagenProducto.setImageBitmap(bitmapCustom)
        } else if (tipo == "Fruta") {
            imagenProducto.setImageResource(R.drawable.fruta)
        } else {
            imagenProducto.setImageResource(R.drawable.verdura)
        }

        // Mostramos la ubicación si se recibió
        if (tieneLatitud && tieneLongitud) {
            val latitud = intent.getDoubleExtra("LATITUD_EXTRA", 0.0)
            val longitud = intent.getDoubleExtra("LONGITUD_EXTRA", 0.0)
            textoUbicacion.text = String.format(
                Locale.getDefault(),
                "Ubicación GPS: %.4f, %.4f",
                latitud,
                longitud
            )
            textoUbicacion.visibility = View.VISIBLE
        } else {
            textoUbicacion.visibility = View.GONE
        }

        // Cerramos esta pantalla para regresar a la anterior.
        botonVolver.setOnClickListener {
            finish()
        }
    }
}
