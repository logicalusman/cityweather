package com.le.cityweather.weatherdetails.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.le.cityweather.R
import com.le.cityweather.domain.WeatherData
import com.le.cityweather.weatherdetails.repository.WeatherDetailsRepository
import com.le.utils.ResourceProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherDetailsViewModel(
    private val weatherDetailsRepository: WeatherDetailsRepository,
    private val resourceProvider: ResourceProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) :
    ViewModel() {

    sealed class WeatherDetailsViewState {
        data class Loading(val show: Boolean) : WeatherDetailsViewState()
        data class Idle(val weatherData: WeatherData) : WeatherDetailsViewState()
        data class ErrorSnackbar(
            val errorMessage: String,
            val action: String
        ) : WeatherDetailsViewState()
    }

    sealed class ViewAction {
        object Finish : ViewAction()
    }

    val viewState = MutableLiveData<WeatherDetailsViewState>()
    val viewAction = MutableLiveData<ViewAction>()

    fun getWeather(cityId: Int) {
        viewState.value = WeatherDetailsViewState.Loading(true)
        viewModelScope.launch(dispatcher) {
            weatherDetailsRepository.getWeather(cityId).fold(
                ifSuccess = {
                    viewState.value = WeatherDetailsViewState.Idle(weatherData = it)
                },
                ifFailure = {
                    viewState.value = WeatherDetailsViewState.Loading(false)
                    viewState.value = WeatherDetailsViewState.ErrorSnackbar(
                        resourceProvider.getString(R.string.error_message),
                        resourceProvider.getString(R.string.action_retry)
                    )
                }
            )

        }
    }

    fun onRetry(cityId: Int) = getWeather(cityId)

    fun onBackPressed() {
        viewAction.value = ViewAction.Finish
    }


}
