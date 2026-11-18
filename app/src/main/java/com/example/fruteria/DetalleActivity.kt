package com.example.fruteria

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
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
        val textoNutricional = findViewById<TextView>(R.id.textoNutricional)
        val botonVolver = findViewById<Button>(R.id.botonVolver)
        val imagenProducto = findViewById<ImageView>(R.id.imagenProducto)

        // Recibimos los datos enviados desde la pantalla principal o el catálogo.
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

        // Consultar información nutricional en la API de Fruityvice
        consultarInformacionNutricional(nombre, textoNutricional)

        // Cerramos esta pantalla para regresar a la anterior.
        botonVolver.setOnClickListener {
            finish()
        }
    }

    private fun consultarInformacionNutricional(nombre: String, textoNutricional: TextView) {
        textoNutricional.text = "Consultando API Fruityvice..."

        lifecycleScope.launch {
            val fruta = FruityviceRepository.buscarFruta(nombre)
            if (fruta != null && fruta.nutritions != null) {
                val n = fruta.nutritions
                textoNutricional.text = """
                    Fruta (API): ${fruta.name ?: nombre}
                    • Calorías: ${n.calories ?: 0.0} kcal
                    • Azúcares: ${n.sugar ?: 0.0} g
                    • Proteínas: ${n.protein ?: 0.0} g
                    • Grasas: ${n.fat ?: 0.0} g
                    • Carbohidratos: ${n.carbohydrates ?: 0.0} g
                """.trimIndent()
            } else {
                textoNutricional.text = "Fruta no disponible en la API de Fruityvice."
            }
        }
    }
}
