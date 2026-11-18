package com.example.fruteria

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

class MainActivity : ComponentActivity() {

    private var fotoPath: String? = null
    private var archivoFotoActual: File? = null

    private lateinit var clienteUbicacion: FusedLocationProviderClient
    private var latitud: Double? = null
    private var longitud: Double? = null

    private lateinit var ivFoto: ImageView
    private lateinit var textoUbicacion: TextView

    // (1) Lanzador para pedir permiso de Cámara
    private val pedirPermisoCamara = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) lanzarCamara()
        else Toast.makeText(
            this,
            "Sin permiso no puedo hacer la foto",
            Toast.LENGTH_SHORT
        ).show()
    }

    // (2) Lanzador para abrir la cámara y guardar la foto tomada
    private val hacerFoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exito: Boolean ->
        val archivo = archivoFotoActual
        if (exito && archivo != null) {
            fotoPath = archivo.absolutePath
            val bitmap = Fotos.cargar(fotoPath)
            ivFoto.setImageBitmap(bitmap)
            ivFoto.visibility = View.VISIBLE
        } else {
            archivo?.delete()
        }
    }

    // (3) Lanzador para pedir permisos de Ubicación GPS
    private val pedirPermisosUbicacion = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        val fina = resultado[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val aproximada = resultado[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fina || aproximada) obtenerUbicacion()
        else Toast.makeText(
            this,
            "Sin permiso no puedo saber dónde estás",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clienteUbicacion = LocationServices.getFusedLocationProviderClient(this)

        // Relacionamos los elementos del diseño con el código.
        val campoNombre = findViewById<EditText>(R.id.campoNombre)
        val campoPrecio = findViewById<EditText>(R.id.campoPrecio)
        val grupoTipo = findViewById<RadioGroup>(R.id.grupoTipo)
        val botonAnadir = findViewById<Button>(R.id.botonAnadir)
        val botonCatalogo = findViewById<Button>(R.id.botonCatalogo)

        val botonCamara: ImageButton = findViewById(R.id.botonCamara)
        val botonUbicacion: ImageButton = findViewById(R.id.botonUbicacion)

        ivFoto = findViewById(R.id.ivFoto)
        textoUbicacion = findViewById(R.id.textoUbicacion)

        // Listeners para los botones de fotos y GPS
        botonCamara.setOnClickListener { pulsarBotonFoto() }
        botonUbicacion.setOnClickListener { pulsarBotonUbicacion() }

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
                val tipo = if (grupoTipo.checkedRadioButtonId == R.id.opcionFruta) {
                    "Fruta"
                } else {
                    "Verdura"
                }

                // Guardamos el alimento en la base de datos de Room con foto y ubicación opcionales.
                val dao = AppDatabase.obtener(this).alimentoDao()
                lifecycleScope.launch {
                    val alimento = Alimento(
                        nombre = nombre,
                        precio = precio,
                        tipo = tipo,
                        latitud = latitud,
                        longitud = longitud,
                        fotoPath = fotoPath
                    )
                    dao.insertar(alimento)
                }

                // Abrimos la ficha y enviamos los datos del producto.
                val intent = Intent(this, DetalleActivity::class.java)

                intent.putExtra("NOMBRE_EXTRA", nombre)
                intent.putExtra("PRECIO_EXTRA", precio)
                intent.putExtra("TIPO_EXTRA", tipo)
                if (fotoPath != null) {
                    intent.putExtra("FOTO_EXTRA", fotoPath)
                }
                if (latitud != null && longitud != null) {
                    intent.putExtra("LATITUD_EXTRA", latitud!!)
                    intent.putExtra("LONGITUD_EXTRA", longitud!!)
                }

                startActivity(intent)

                Toast.makeText(
                    this,
                    "Producto añadido: $nombre",
                    Toast.LENGTH_SHORT
                ).show()

                // Limpiamos el formulario y reseteamos variables para añadir otro producto.
                campoNombre.text.clear()
                campoPrecio.text.clear()
                grupoTipo.clearCheck()
                fotoPath = null
                archivoFotoActual = null
                latitud = null
                longitud = null
                ivFoto.setImageDrawable(null)
                ivFoto.visibility = View.GONE
                textoUbicacion.text = "Sin ubicación"
            }
        }
    }

    private fun pulsarBotonFoto() {
        val permiso = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (permiso == PackageManager.PERMISSION_GRANTED) {
            lanzarCamara()
        } else {
            pedirPermisoCamara.launch(Manifest.permission.CAMERA)
        }
    }

    private fun lanzarCamara() {
        val archivo = Fotos.crearArchivo(this)
        archivoFotoActual = archivo
        hacerFoto.launch(Fotos.uriDe(this, archivo))
    }

    private fun tienePermisoUbicacion(): Boolean {
        val fina = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val aprox = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fina == PackageManager.PERMISSION_GRANTED || aprox == PackageManager.PERMISSION_GRANTED
    }

    private fun pulsarBotonUbicacion() {
        if (tienePermisoUbicacion()) {
            obtenerUbicacion()
        } else {
            pedirPermisosUbicacion.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun obtenerUbicacion() {
        clienteUbicacion
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { ubicacion: Location? ->
                if (ubicacion != null) {
                    latitud = ubicacion.latitude
                    longitud = ubicacion.longitude
                    textoUbicacion.text = String.format(
                        Locale.getDefault(),
                        "Ubicación: %.4f, %.4f",
                        ubicacion.latitude,
                        ubicacion.longitude
                    )
                } else {
                    Toast.makeText(
                        this,
                        "No se ha podido obtener la ubicación",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
