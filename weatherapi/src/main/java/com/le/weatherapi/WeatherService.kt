package com.le.weatherapi

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherService(private val context: Context) {

    private val cityList: List<City> by lazy {
        val cityListJson = readCityListFromJsonFile()
        Gson().fromJson<Array<JsonCity>>(cityListJson, Array<JsonCity>::class.java).map {
            it.toCity()
        }
    }

    suspend fun getWorldCitiesList(): List<City> {
        return withContext(Dispatchers.IO) {
            cityList
        }
    }


    suspend fun searchCities(search: String): List<City> = withContext(Dispatchers.IO) {
        cityList.filter { it.name.contains(other = search, ignoreCase = true) }
    }

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
