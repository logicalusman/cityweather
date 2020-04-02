package com.le.cityweather.main.repository

import com.le.cityweather.model.CityData
import com.le.weatherapi.City
import com.le.weatherapi.WeatherService
import java.util.*

class MainRepository(private val service: WeatherService) {

    suspend fun getCities(): List<CityData> =
        service.getWorldCitiesList().map { it.toCityData() }

    suspend fun searchCities(search: String): List<CityData> =
        service.searchCities(search).map { it.toCityData() }

}

private fun City.toCityData() =
    CityData(
        id = id,
        name = name,
        state = state,
        countryCode = countryCode,
        countryName = countryCode.countryName()
    )

private fun String.countryName(): String = if (isNullOrBlank()) "" else Locale("", this).displayName