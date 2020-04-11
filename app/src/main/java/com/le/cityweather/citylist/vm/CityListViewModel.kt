package com.le.cityweather.citylist.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.le.cityweather.citylist.repository.CityListRepository
import com.le.cityweather.domain.CityData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CityListViewModel(private val repository: CityListRepository) : ViewModel() {

    sealed class MainViewState {
        object Loading : MainViewState()
        data class Idle(val data: List<CityData>) : MainViewState()
    }

    sealed class Action {
        data class CityClicked(val cityId: Int) : Action()
    }

    val viewState = MutableLiveData<MainViewState>()
    val viewAction = MutableLiveData<Action>()

    fun getCityList() {
        viewState.apply {
            value = MainViewState.Loading
            viewModelScope.launch(Dispatchers.Main) {
                value = MainViewState.Idle(repository.getCities())
            }
        }
    }

    fun searchCity(searchText: String) {
        viewModelScope.launch(Dispatchers.Main) {
            viewState.value = MainViewState.Idle(repository.searchCities(searchText))
        }
    }

    fun onCityClicked(cityData: CityData) {
        viewAction.value = Action.CityClicked(cityData.id)
    }
}
