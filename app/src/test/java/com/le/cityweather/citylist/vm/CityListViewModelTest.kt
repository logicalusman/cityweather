package com.le.cityweather.citylist.vm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.le.cityweather.citylist.repository.CityListRepository
import com.le.cityweather.citylist.vm.CityListViewModel.Action
import com.le.cityweather.citylist.vm.CityListViewModel.Action.CityClicked
import com.le.cityweather.citylist.vm.CityListViewModel.CityListViewState
import com.le.cityweather.citylist.vm.CityListViewModel.CityListViewState.Idle
import com.le.cityweather.citylist.vm.CityListViewModel.CityListViewState.Loading
import com.le.cityweather.domain.CityData
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

private val london = CityData(1, "London", "", "GB")
private val newYork = CityData(2, "New York", "NJ", "US")
private val saintBabel = CityData(3, "Saint Babel", "", "FR")
private val sunset = CityData(4, "Sunset", "FL", "US")
private val baraka = CityData(5, "Baraka", "", "CD")
private val cityList: List<CityData> = listOf(london, newYork, saintBabel, sunset, baraka)
private const val citySearchString = "o"
private val searchedCityList: List<CityData> = listOf(london, newYork)
private val testDispatcher = TestCoroutineDispatcher()

class CityListViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val cityListRepository: CityListRepository = mockk {
        coEvery { getCities() } answers { cityList }
        coEvery { searchCities(citySearchString) } answers { searchedCityList }
    }
    private val capturedViewStates = mutableListOf<CityListViewState>()
    private val mockViewStateObserver: Observer<CityListViewState> = mockk {
        every { onChanged(capture(capturedViewStates)) } answers { Unit }
    }
    private val capturedActions = mutableListOf<Action>()
    private val mockActionObserver: Observer<Action> = mockk {
        every { onChanged(capture(capturedActions)) } answers { Unit }
    }
    private val cityListViewModel = CityListViewModel(
        repository = cityListRepository,
        dispatcher = testDispatcher
    )

    @Test
    fun `notifies loading and idle state with data to the observer when gets city list`() {
        cityListViewModel.viewState.observeForever(mockViewStateObserver)
        testDispatcher.runBlockingTest {
            cityListViewModel.getCityList()

            assertEquals(listOf(Loading, Idle(cityList)), capturedViewStates)
        }
    }

    @Test
    fun `notifies idle state with searched city list to the observer`() {
        cityListViewModel.viewState.observeForever(mockViewStateObserver)
        testDispatcher.runBlockingTest {
            cityListViewModel.searchCity(citySearchString)

            assertEquals(listOf(Idle(listOf(london, newYork))), capturedViewStates)
        }
    }

    @Test
    fun `notifies city id to the observer on city click action`() {
        cityListViewModel.viewAction.observeForever(mockActionObserver)
        testDispatcher.runBlockingTest {
            cityListViewModel.onCityClicked(london)

            assertEquals(listOf(CityClicked(1)), capturedActions)
        }
    }

}
