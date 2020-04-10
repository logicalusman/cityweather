package com.le.weatherapi

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.le.utils.MissingResponseBody
import com.le.utils.NetworkFailure
import com.le.utils.Result
import com.le.utils.asSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.UnknownHostException

class WeatherService(private val context: Context) {

    private val cityList: List<City> by lazy {
        val cityListJson = readCityListFromJsonFile()
        Gson().fromJson<Array<JsonCity>>(cityListJson, Array<JsonCity>::class.java).map {
            it.toCity()
        }
    }

    private val openWeatherService: OpenWeatherService by lazy {
        Retrofit.Builder()
            .baseUrl(OPEN_WEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build().create(OpenWeatherService::class.java)
    }

    suspend fun getWorldCitiesList(): List<City> = withContext(Dispatchers.IO) {
        cityList
    }

    suspend fun searchCities(search: String): List<City> = withContext(Dispatchers.IO) {
        cityList.filter { it.name.contains(other = search, ignoreCase = true) }
    }

    suspend fun <T : Result<Weather>> getWeather(cityId: Int): T =
        withContext(Dispatchers.IO) {
            try {
                val response: Response<OpenWeatherResponse> = openWeatherService.getWeather(cityId)
                if (response.isSuccessful) {
                    (response.toWeatherData() ?: toMissingErrorBody()) as T
                } else {
                    toNetworkFailure(response.errorBody()?.string()) as T
                }
            } catch (e: UnknownHostException) {
                toNetworkFailure(e.message) as T
            }
        }

    private fun toNetworkFailure(errorMessage: String? = null) =
        Result.Failure<Nothing>(error = NetworkFailure(errorMessage))

    private fun toMissingErrorBody() = Result.Failure<Nothing>(error = MissingResponseBody())

    private fun readCityListFromJsonFile(): String =
        context.assets.open("city.list.json").bufferedReader().use {
            it.readText()
        }
}

private data class JsonCity(
    val id: Int,
    val name: String,
    val state: String,
    val country: String,
    val coord: JsonCoord
)

private data class JsonCoord(val lon: Double, val lat: Double)

private fun JsonCity.toCity() =
    City(id = id, name = name, state = state, countryCode = country)

private fun Response<OpenWeatherResponse>.toWeatherData() = this.body()?.toWeather()?.asSuccess()