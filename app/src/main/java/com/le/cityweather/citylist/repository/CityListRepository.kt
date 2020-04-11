package com.le.cityweather.citylist.repository

import com.le.cityweather.domain.CityData
import com.le.weatherapi.City
import com.le.weatherapi.WeatherService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainRepository(private val service: WeatherService) {

    suspend fun getCities(): List<CityData> =
        service.getWorldCitiesList().map { it.toCityData() }

    suspend fun searchCities(search: String): List<CityData> =
        service.searchCities(search).map {
            withContext(Dispatchers.IO) {
                it.toCityData()
            }
        }

}

private fun City.toCityData() =
    CityData(
        id = id,
        name = name,
        state = state,
        countryCode = countryCode
    )
