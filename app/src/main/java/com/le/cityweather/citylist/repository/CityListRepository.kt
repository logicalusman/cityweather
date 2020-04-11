package com.le.cityweather.citylist.repository

import com.le.cityweather.domain.CityData
import com.le.weatherapi.City
import com.le.weatherapi.WeatherService

class CityListRepository(private val service: WeatherService) {

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
        countryCode = countryCode
    )
