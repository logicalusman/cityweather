package com.le.weatherapi

import android.content.Context
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

class WeatherService(
    private val context: Context,
    private val cityListFromJson: CityListFromLocalJson = CityListFromLocalJson(context),
    private val openWeatherService: OpenWeatherService = Retrofit.Builder()
        .baseUrl(OPEN_WEATHER_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build().create(OpenWeatherService::class.java)
) {

    suspend fun getWorldCitiesList(): List<City> = withContext(Dispatchers.IO) {
        cityListFromJson.cityList
    }

    suspend fun searchCities(search: String): List<City> = withContext(Dispatchers.IO) {
        cityListFromJson.cityList.filter { it.name.contains(other = search, ignoreCase = true) }
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
            } catch (e: Exception) {
                toNetworkFailure(e.message) as T
            }
        }

    private fun toNetworkFailure(errorMessage: String? = null) =
        Result.Failure<Nothing>(error = NetworkFailure(errorMessage))

    private fun toMissingErrorBody() = Result.Failure<Nothing>(error = MissingResponseBody())

}

private fun Response<OpenWeatherResponse>.toWeatherData() = this.body()?.toWeather()?.asSuccess()
