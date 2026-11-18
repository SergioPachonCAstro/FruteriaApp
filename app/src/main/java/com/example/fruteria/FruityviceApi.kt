package com.example.fruteria

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

// Modelos de datos para mapear la respuesta JSON de Fruityvice
data class Nutritions(
    val calories: Double? = 0.0,
    val fat: Double? = 0.0,
    val sugar: Double? = 0.0,
    val carbohydrates: Double? = 0.0,
    val protein: Double? = 0.0
)

data class FruitResponse(
    val id: Int? = null,
    val name: String? = null,
    val family: String? = null,
    val order: String? = null,
    val genus: String? = null,
    val nutritions: Nutritions? = null
)

// Interfaz Retrofit para interactuar con la API
interface FruityviceApi {

    @GET("api/fruit/all")
    suspend fun getTodasLasFrutas(): Response<List<FruitResponse>>

    @GET("api/fruit/{name}")
    suspend fun getFrutaPorNombre(@Path("name") nombre: String): Response<FruitResponse>
}

// Cliente Singleton de Retrofit
object RetrofitClient {
    private const val BASE_URL = "https://www.fruityvice.com/"

    val instance: FruityviceApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FruityviceApi::class.java)
    }
}

// Helper para obtener la información nutricional de una fruta
object FruityviceRepository {

    // Diccionario de traducción de frutas comunes de Español a Inglés para Fruityvice
    private val traduccionesEspanolIngles = mapOf(
        "manzana" to "Apple",
        "platano" to "Banana",
        "plátano" to "Banana",
        "banana" to "Banana",
        "fresa" to "Strawberry",
        "naranja" to "Orange",
        "limon" to "Lemon",
        "limón" to "Lemon",
        "piña" to "Pineapple",
        "pina" to "Pineapple",
        "pera" to "Pear",
        "uva" to "Grape",
        "uvas" to "Grape",
        "melocoton" to "Peach",
        "melocotón" to "Peach",
        "durazno" to "Peach",
        "sandia" to "Watermelon",
        "sandía" to "Watermelon",
        "melon" to "Melon",
        "melón" to "Melon",
        "arandano" to "Blueberry",
        "arándano" to "Blueberry",
        "frambuesa" to "Raspberry",
        "cereza" to "Cherry",
        "higo" to "Fig",
        "ciruela" to "Plum",
        "kiwi" to "Kiwi",
        "aguacate" to "Avocado",
        "tomate" to "Tomato",
        "lima" to "Lime",
        "mango" to "Mango",
        "papaya" to "Papaya",
        "granada" to "Pomegranate",
        "mora" to "Blackberry",
        "coco" to "Coconut",
        "maracuya" to "Passionfruit",
        "maracuyá" to "Passionfruit"
    )

    suspend fun buscarFruta(nombreUsuario: String): FruitResponse? {
        val nombreLimpio = nombreUsuario.trim().lowercase()
        if (nombreLimpio.isEmpty()) return null

        // 1. Verificar si hay traducción al inglés
        val nombreIngles = traduccionesEspanolIngles[nombreLimpio] ?: nombreUsuario.trim()

        try {
            // Intentar buscar directamente por el nombre traducido u original
            val respuestaDirecta = RetrofitClient.instance.getFrutaPorNombre(nombreIngles)
            if (respuestaDirecta.isSuccessful && respuestaDirecta.body() != null) {
                return respuestaDirecta.body()
            }

            // Si la búsqueda directa falla, descargamos la lista completa y buscamos coincidencias
            val respuestaTodas = RetrofitClient.instance.getTodasLasFrutas()
            if (respuestaTodas.isSuccessful && respuestaTodas.body() != null) {
                val listaFrutas = respuestaTodas.body()!!
                return listaFrutas.firstOrNull { fruta ->
                    val nameApi = fruta.name?.lowercase() ?: ""
                    nameApi == nombreLimpio || nameApi == nombreIngles.lowercase() ||
                            nameApi.contains(nombreLimpio) || nombreLimpio.contains(nameApi)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
}
