package com.example.fruteria

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.Locale

// Modelos de datos para mapear la respuesta JSON de CalorTracker API
data class CalorTrackerNutrientsPer100g(
    val energyKcal: Double? = 0.0,
    val proteinG: Double? = 0.0,
    val fatG: Double? = 0.0,
    val carbsG: Double? = 0.0,
    val sugarsG: Double? = 0.0
)

data class CalorTrackerFoodData(
    val slug: String? = null,
    val name: String? = null,
    val category: String? = null,
    val per100g: CalorTrackerNutrientsPer100g? = null,
    val url: String? = null,
    val apiUrl: String? = null
)

data class CalorTrackerAttribution(
    val text: String? = null,
    val url: String? = null,
    val required: Boolean? = true
)

data class CalorTrackerSingleResponse(
    val data: CalorTrackerFoodData? = null,
    val attribution: CalorTrackerAttribution? = null
)

data class CalorTrackerSearchResponse(
    val data: List<CalorTrackerFoodData>? = null,
    val attribution: CalorTrackerAttribution? = null
)

// Interfaz Retrofit para interactuar con la API de CalorTracker
interface CalorTrackerApi {

    @GET("api/v1/foods")
    suspend fun buscarComidas(@Query("q") query: String): Response<CalorTrackerSearchResponse>

    @GET("api/v1/foods/{slug}")
    suspend fun getComidaPorSlug(@Path("slug") slug: String): Response<CalorTrackerSingleResponse>
}

// Cliente Singleton de Retrofit para CalorTracker
object CalorTrackerRetrofitClient {
    private const val BASE_URL = "https://calortracker.com/"

    val instance: CalorTrackerApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CalorTrackerApi::class.java)
    }
}

data class VerduraNutricionalResult(
    val nombreEspanol: String,
    val per100g: CalorTrackerNutrientsPer100g,
    val atribucion: String
)

// Helper para obtener la información nutricional de una verdura usando CalorTracker
object CalorTrackerRepository {

    // Diccionario de traducción de verduras de Español a Inglés para la API
    private val traduccionesEspanolIngles = mapOf(
        "brócoli" to "broccoli",
        "brocoli" to "broccoli",
        "zanahoria" to "carrot",
        "zanahorias" to "carrot",
        "espinaca" to "spinach",
        "espinacas" to "spinach",
        "tomate" to "tomato",
        "tomates" to "tomato",
        "pepino" to "cucumber",
        "pepinos" to "cucumber",
        "cebolla" to "onion",
        "cebollas" to "onion",
        "ajo" to "garlic",
        "ajos" to "garlic",
        "pimiento" to "pepper",
        "pimientos" to "pepper",
        "lechuga" to "lettuce",
        "lechugas" to "lettuce",
        "patata" to "potato",
        "patatas" to "potato",
        "papa" to "potato",
        "papas" to "potato",
        "calabaza" to "pumpkin",
        "calabazas" to "pumpkin",
        "calabacín" to "zucchini",
        "calabacin" to "zucchini",
        "calabacines" to "zucchini",
        "berenjena" to "eggplant",
        "berenjenas" to "eggplant",
        "coliflor" to "cauliflower",
        "espárrago" to "asparagus",
        "esparrago" to "asparagus",
        "espárragos" to "asparagus",
        "esparragos" to "asparagus",
        "apio" to "celery",
        "rábano" to "radish",
        "rabano" to "radish",
        "rábanos" to "radish",
        "rabanos" to "radish",
        "guisante" to "peas",
        "guisantes" to "peas",
        "judía verde" to "green beans",
        "judia verde" to "green beans",
        "judías verdes" to "green beans",
        "judias verdes" to "green beans",
        "alcachofa" to "artichoke",
        "alcachofas" to "artichoke",
        "repollo" to "cabbage",
        "col" to "cabbage",
        "champiñón" to "mushroom",
        "champinon" to "mushroom",
        "champiñones" to "mushroom",
        "seta" to "mushroom",
        "setas" to "mushroom",
        "remolacha" to "beetroot",
        "nabo" to "turnip",
        "nabos" to "turnip",
        "puerro" to "leek",
        "puerros" to "leek",
        "boniato" to "sweet potato",
        "batata" to "sweet potato",
        "maíz" to "corn",
        "maiz" to "corn"
    )

    // Nombres formateados en español
    private val nombresEnEspanol = mapOf(
        "broccoli" to "Brócoli",
        "carrot" to "Zanahoria",
        "spinach" to "Espinacas",
        "tomato" to "Tomate",
        "cucumber" to "Pepino",
        "onion" to "Cebolla",
        "garlic" to "Ajo",
        "pepper" to "Pimiento",
        "lettuce" to "Lechuga",
        "potato" to "Patata",
        "pumpkin" to "Calabaza",
        "zucchini" to "Calabacín",
        "eggplant" to "Berenjena",
        "cauliflower" to "Coliflor",
        "asparagus" to "Espárrago",
        "celery" to "Apio",
        "radish" to "Rábano",
        "peas" to "Guisantes",
        "green beans" to "Judías verdes",
        "artichoke" to "Alcachofa",
        "cabbage" to "Repollo",
        "mushroom" to "Champiñón",
        "beetroot" to "Remolacha",
        "turnip" to "Nabo",
        "leek" to "Puerro",
        "sweet potato" to "Boniato",
        "corn" to "Maíz"
    )

    suspend fun buscarVerdura(nombreUsuario: String): VerduraNutricionalResult? {
        val nombreLimpio = nombreUsuario.trim().lowercase(Locale.getDefault())
        if (nombreLimpio.isEmpty()) return null

        val terminoIngles = traduccionesEspanolIngles[nombreLimpio] ?: nombreLimpio

        try {
            // 1. Buscar en la API por el término en inglés
            var respuesta = CalorTrackerRetrofitClient.instance.buscarComidas(terminoIngles)
            var lista = respuesta.body()?.data

            // Si no encuentra nada, intentar buscar con la palabra original del usuario
            if (lista.isNullOrEmpty() && terminoIngles != nombreLimpio) {
                respuesta = CalorTrackerRetrofitClient.instance.buscarComidas(nombreLimpio)
                lista = respuesta.body()?.data
            }

            val primerItem = lista?.firstOrNull()

            if (primerItem != null) {
                val slug = primerItem.slug ?: terminoIngles
                val atribucionText = respuesta.body()?.attribution?.text
                    ?: "Data: USDA FoodData Central via CalorTracker"

                // Obtener detalle completo por slug para asegurar tener sugarsG
                var nutrientes: CalorTrackerNutrientsPer100g? = null
                try {
                    val respuestaDetalle = CalorTrackerRetrofitClient.instance.getComidaPorSlug(slug)
                    if (respuestaDetalle.isSuccessful && respuestaDetalle.body()?.data != null) {
                        nutrientes = respuestaDetalle.body()?.data?.per100g
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (nutrientes == null) {
                    nutrientes = primerItem.per100g ?: CalorTrackerNutrientsPer100g()
                }

                // Determinar el nombre a mostrar en español
                val nombreEspanol = obtenerNombreEspanol(nombreUsuario, primerItem.name, slug)

                return VerduraNutricionalResult(
                    nombreEspanol = nombreEspanol,
                    per100g = nutrientes,
                    atribucion = atribucionText
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    private fun obtenerNombreEspanol(nombreUsuario: String, apiName: String?, slug: String?): String {
        val userLower = nombreUsuario.trim().lowercase(Locale.getDefault())

        // Si el usuario introdujo un nombre en español que conocemos, capitalizarlo
        if (traduccionesEspanolIngles.containsKey(userLower)) {
            val key = traduccionesEspanolIngles[userLower]!!
            if (nombresEnEspanol.containsKey(key)) {
                return nombresEnEspanol[key]!!
            }
            return nombreUsuario.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }

        // Buscar por slug
        if (slug != null && nombresEnEspanol.containsKey(slug)) {
            return nombresEnEspanol[slug]!!
        }

        // Buscar por apiName
        val apiLower = apiName?.lowercase(Locale.getDefault()) ?: ""
        for ((eng, esp) in nombresEnEspanol) {
            if (apiLower.contains(eng)) return esp
        }

        // Si no se encuentra en el mapa, capitalizar la entrada del usuario
        return nombreUsuario.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}
