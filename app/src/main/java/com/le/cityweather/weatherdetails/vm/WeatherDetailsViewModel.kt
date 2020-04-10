package com.le.cityweather.weatherdetails.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.le.cityweather.R
import com.le.cityweather.common.ResourceProvider
import com.le.cityweather.domain.WeatherData
import com.le.cityweather.weatherdetails.repository.WeatherDetailsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherDetailsViewModel(
    private val weatherDetailsRepository: WeatherDetailsRepository,
    private val resourceProvider: ResourceProvider
) :
    ViewModel() {

    sealed class ViewState {
        data class Loading(val show: Boolean) : ViewState()
        data class Idle(val weatherData: WeatherData) : ViewState()
        data class ErrorSnackbar(
            val errorMessage: String,
            val action: String,
            val onActionClick: () -> Unit
        ) : ViewState()
    }

    sealed class ViewAction {
        object Finish : ViewAction()
    }

    val viewState = MutableLiveData<ViewState>()
    val viewAction = MutableLiveData<ViewAction>()

    fun getWeather(cityId: Int) {
        viewState.value = ViewState.Loading(true)
        viewModelScope.launch(Dispatchers.Main) {
            weatherDetailsRepository.getWeather(cityId).fold(
                ifSuccess = {
                    viewState.value = ViewState.Idle(weatherData = it)
                },
                ifFailure = {
                    viewState.value = ViewState.Loading(false)
                    viewState.value = ViewState.ErrorSnackbar(
                        resourceProvider.getString(R.string.error_message),
                        resourceProvider.getString(R.string.action_retry)
                    ) {
                        getWeather(cityId)
                    }
                }
            )

        }
    }

    fun onBackPressed() {
        viewAction.value = ViewAction.Finish
    }


}
