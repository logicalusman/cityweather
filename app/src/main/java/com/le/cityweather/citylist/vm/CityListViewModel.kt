package com.le.cityweather.citylist.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.le.cityweather.citylist.repository.CityListRepository
import com.le.cityweather.domain.CityData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CityListViewModel(
    private val repository: CityListRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) :
    ViewModel() {

    sealed class CityListViewState {
        object Loading : CityListViewState()
        data class Idle(val data: List<CityData>) : CityListViewState()
    }

    sealed class Action {
        data class CityClicked(val cityId: Int) : Action()
    }

    val viewState = MutableLiveData<CityListViewState>()
    val viewAction = MutableLiveData<Action>()

    fun getCityList() {
        viewState.apply {
            value = CityListViewState.Loading
            viewModelScope.launch(dispatcher) {
                value = CityListViewState.Idle(repository.getCities())
            }
        }
    }

    fun searchCity(searchText: String) {
        viewModelScope.launch(dispatcher) {
            viewState.value = CityListViewState.Idle(repository.searchCities(searchText))
        }
    }

    fun onCityClicked(cityData: CityData) {
        viewAction.value = Action.CityClicked(cityData.id)
    }
}
