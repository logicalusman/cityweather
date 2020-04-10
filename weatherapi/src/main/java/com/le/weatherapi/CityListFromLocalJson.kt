package com.le.weatherapi

import android.content.Context
import com.google.gson.Gson

object CityListFromLocalJson {

    private lateinit var context: Context

    operator fun invoke(context: Context): CityListFromLocalJson {
        this.context = context
        return this
    }

    internal val cityList: List<City> by lazy {
        val cityListJson = readCityListFromJsonFile()
        Gson().fromJson<Array<JsonCity>>(cityListJson, Array<JsonCity>::class.java).map {
            it.toCity()
        }
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