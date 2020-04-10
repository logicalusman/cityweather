package com.le.cityweather.weatherdetails.repository

import com.le.cityweather.domain.WeatherData
import com.le.utils.Result
import com.le.weatherapi.Weather
import com.le.weatherapi.WeatherService

class WeatherDetailsRepository(private val service: WeatherService) {

    suspend fun getWeather(cityId: Int) = service.getWeather<Result<Weather>>(cityId).map {
        it.toWeatherData()
    }


}

private fun Weather.toWeatherData() =
    WeatherData(location = locationName, status = status, description = description)
