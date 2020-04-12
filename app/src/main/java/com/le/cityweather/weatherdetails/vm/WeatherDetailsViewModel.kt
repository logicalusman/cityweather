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

    sealed class ViewState {
        data class Loading(val show: Boolean) : ViewState()
        data class Idle(val weatherData: WeatherData) : ViewState()
        data class ErrorSnackbar(
            val errorMessage: String,
            val action: String
        ) : ViewState()
    }

    sealed class ViewAction {
        object Finish : ViewAction()
    }

    val viewStateLiveData = MutableLiveData<ViewState>()
    val viewActionLiveData = MutableLiveData<ViewAction>()

    fun getWeather(cityId: Int) {
        viewStateLiveData.value = ViewState.Loading(true)
        viewModelScope.launch(dispatcher) {
            weatherDetailsRepository.getWeather(cityId).fold(
                ifSuccess = {
                    viewStateLiveData.value = ViewState.Idle(weatherData = it)
                },
                ifFailure = {
                    viewStateLiveData.value = ViewState.Loading(false)
                    viewStateLiveData.value = ViewState.ErrorSnackbar(
                        resourceProvider.getString(R.string.error_message),
                        resourceProvider.getString(R.string.action_retry)
                    )
                }
            )

        }
    }

    fun onRetry(cityId: Int) = getWeather(cityId)

    fun onBackPressed() {
        viewActionLiveData.value = ViewAction.Finish
    }


}
