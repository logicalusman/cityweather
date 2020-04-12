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

    sealed class ViewState {
        object Loading : ViewState()
        data class Idle(val data: List<CityData>) : ViewState()
    }

    sealed class ViewAction {
        data class CityClicked(val cityId: Int) : ViewAction()
    }

    val viewStateLiveData = MutableLiveData<ViewState>()
    val viewActionLiveData = MutableLiveData<ViewAction>()

    fun getCityList() {
        viewStateLiveData.apply {
            value = ViewState.Loading
            viewModelScope.launch(dispatcher) {
                value = ViewState.Idle(repository.getCities())
            }
        }
    }

    fun searchCity(searchText: String) {
        viewModelScope.launch(dispatcher) {
            viewStateLiveData.value = ViewState.Idle(repository.searchCities(searchText))
        }
    }

    fun onCityClicked(cityData: CityData) {
        viewActionLiveData.value = ViewAction.CityClicked(cityData.id)
    }
}
