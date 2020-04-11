package com.le.weatherapi

import com.le.utils.NetworkFailure
import com.le.utils.Result
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Test
import retrofit2.Response
import java.net.UnknownHostException

private val london = City(1, "London", "", "GB")
private val newYork = City(2, "New York", "NJ", "US")
private val saintBabel = City(3, "Saint Babel", "", "FR")
private val sunset = City(4, "Sunset", "FL", "US")
private val baraka = City(5, "Baraka", "", "CD")
private val jsonCityList: List<City> = listOf(london, newYork, saintBabel, sunset, baraka)
private val openWeatherResponse = OpenWeatherResponse(
    "London",
    ApiCoord(0.0, 0.0),
    listOf(
        ApiWeather(1, "Rain", "light rain", "")
    ), 0
)

class WeatherServiceTest {

    private val response: Response<OpenWeatherResponse> = mockk()
    private val cityListFromJson: CityListFromLocalJson = mockk {
        every { cityList } answers { jsonCityList }
    }
    private val openWeatherService: OpenWeatherService = mockk()
    private val weatherService = WeatherService(mockk(), cityListFromJson, openWeatherService)

    @Test
    fun `get cities list from local json`() {
        runBlocking {
            val list = weatherService.getWorldCitiesList()

            assertEquals(jsonCityList, list)
        }
    }

    @Test
    fun `search city matching the given search string`() {
        runBlocking {
            val list = weatherService.searchCities("london")

            assertEquals(listOf(london), list)
        }
    }

    @Test
    fun `search cities matching the given search string`() {
        runBlocking {
            val list = weatherService.searchCities("n")

            assertEquals(listOf(london, newYork, saintBabel, sunset), list)
        }
    }

    @Test
    fun `returns empty list when search string doesn't match any city in the json list`() {
        runBlocking {
            val list = weatherService.searchCities("zzz")

            assertEquals(emptyList<City>(), list)
        }
    }

    @Test
    fun `returns weather result when the api successfully fetches weather data for the given city`() {
        every { response.isSuccessful } answers { true }
        every { response.body() } answers { openWeatherResponse }
        coEvery { openWeatherService.getWeather(1) } answers { response }

        runBlocking {
            val result: Result<Weather> = weatherService.getWeather(1)

            assertThat(result, instanceOf(Result.Success::class.java))
            assertEquals(
                Weather("London", "Rain", "light rain"),
                (result as Result.Success<Weather>).data
            )
        }
    }

    @Test
    fun `returns network failure result when the api call fails with network error while fetching weather data for the given city`() {
        every { response.isSuccessful } answers { false }
        coEvery { openWeatherService.getWeather(1) } answers { throw UnknownHostException() }

        runBlocking {
            val result: Result<Weather> = weatherService.getWeather(1)

            assertThat(result, instanceOf(Result.Failure::class.java))
            assertThat(
                (result as Result.Failure<Weather>).error,
                instanceOf(NetworkFailure::class.java)
            )
        }
    }

}
