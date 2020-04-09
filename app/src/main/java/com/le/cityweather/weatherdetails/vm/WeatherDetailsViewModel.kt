package com.le.cityweather.weatherdetails.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.le.cityweather.domain.WeatherData
import com.le.cityweather.weatherdetails.repository.WeatherDetailsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherDetailsViewModel(private val weatherDetailsRepository: WeatherDetailsRepository) :
    ViewModel() {

    sealed class ViewState {
        object Loading : ViewState()
        data class Idle(val weatherData: WeatherData) : ViewState()
    }

    sealed class ViewAction {
        object Finish : ViewAction()
    }

    val viewState = MutableLiveData<ViewState>()
    val viewAction = MutableLiveData<ViewAction>()

    fun getWeather(cityId: Int) {
        viewState.value = ViewState.Loading
        viewModelScope.launch(Dispatchers.Main) {
            viewState.value =
                ViewState.Idle(weatherData = weatherDetailsRepository.getWeather(cityId))
        }
    }

    fun onBackPressed() {
        viewAction.value = ViewAction.Finish
    }


}
