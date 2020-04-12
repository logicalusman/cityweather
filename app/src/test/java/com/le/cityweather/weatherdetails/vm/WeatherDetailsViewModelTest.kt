package com.le.cityweather.weatherdetails.vm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.le.cityweather.domain.WeatherData
import com.le.cityweather.weatherdetails.repository.WeatherDetailsRepository
import com.le.cityweather.weatherdetails.vm.WeatherDetailsViewModel.ViewAction
import com.le.cityweather.weatherdetails.vm.WeatherDetailsViewModel.WeatherDetailsViewState
import com.le.cityweather.weatherdetails.vm.WeatherDetailsViewModel.WeatherDetailsViewState.Loading
import com.le.utils.NetworkFailure
import com.le.utils.ResourceProvider
import com.le.utils.Result
import com.le.utils.asSuccess
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString

private const val cityId = 1
private val weatherData = WeatherData("London", "Raining", "light rain")
private val testDispatcher = TestCoroutineDispatcher()

class WeatherDetailsViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val capturedViewStates = mutableListOf<WeatherDetailsViewState>()
    private val mockViewStateObserver: Observer<WeatherDetailsViewState> = mockk {
        every { onChanged(capture(capturedViewStates)) } answers { Unit }
    }
    private val capturedActionStates = mutableListOf<ViewAction>()
    private val mockViewActionStateObserver: Observer<ViewAction> = mockk {
        every { onChanged(capture(capturedActionStates)) } answers { Unit }
    }
    private val resourceProvider: ResourceProvider = mockk {
        every { getString(any()) } answers { anyString() }
    }
    private val weatherDetailsRepository: WeatherDetailsRepository = mockk()
    private val weatherDetailsViewModel = WeatherDetailsViewModel(
        weatherDetailsRepository = weatherDetailsRepository,
        resourceProvider = resourceProvider,
        dispatcher = testDispatcher
    )

    @Test
    fun `notifies loading and idle state with data to the observer when gets weather of the given city id successfully`() {
        coEvery { weatherDetailsRepository.getWeather(cityId) } answers { weatherData.asSuccess() }
        weatherDetailsViewModel.viewState.observeForever(mockViewStateObserver)
        testDispatcher.runBlockingTest {
            weatherDetailsViewModel.getWeather(cityId)

            assertEquals(listOf(Loading(true), WeatherDetailsViewState.Idle(weatherData)), capturedViewStates)
        }
    }

    @Test
    fun `notifies loading and error states to the observer when fails to get weather of the given city id`() {
        coEvery { weatherDetailsRepository.getWeather(cityId) } answers { Result.Failure(NetworkFailure()) }
        weatherDetailsViewModel.viewState.observeForever(mockViewStateObserver)
        testDispatcher.runBlockingTest {
            weatherDetailsViewModel.getWeather(cityId)

            assertEquals(
                mutableListOf(
                    Loading(true),
                    Loading(false),
                    WeatherDetailsViewState.ErrorSnackbar(anyString(), anyString())
                ),
                capturedViewStates
            )
        }
    }

    @Test
    fun `notifies finish state to the observer when on back pressed`() {
        weatherDetailsViewModel.viewAction.observeForever(mockViewActionStateObserver)
        testDispatcher.runBlockingTest {
            weatherDetailsViewModel.onBackPressed()

            assertEquals(mutableListOf(ViewAction.Finish), capturedActionStates)
        }
    }

}
