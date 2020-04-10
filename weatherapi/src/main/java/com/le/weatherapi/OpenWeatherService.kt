package com.le.weatherapi

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

private const val API_KEY = "4c59fa5458109c9cfe7678d794160515"
internal const val OPEN_WEATHER_BASE_URL = "https://api.openweathermap.org/"

interface OpenWeatherService {

    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("id") cityId: Int,
        @Query("appid") appId: String = API_KEY
    ): Response<OpenWeatherResponse>
}

data class OpenWeatherResponse(
    val name: String,
    val coord: ApiCoord,
    val weather: List<ApiWeather>,
    val cod: Int
)

data class ApiCoord(val lon: Double, val lat: Double)

data class ApiWeather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

fun OpenWeatherResponse.toWeather() = Weather(
    locationName = name,
    status = weather.first().main,
    description = weather.first().description
)
