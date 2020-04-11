package com.le.cityweather.citylist.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.le.cityweather.citylist.repository.CityListRepository
import com.le.cityweather.citylist.ui.CityListFragment
import com.le.cityweather.citylist.vm.CityListViewModel
import com.le.weatherapi.WeatherService

fun CityListFragment.createMainViewModel(): CityListViewModel = ViewModelProvider(
    this,
    MainViewModelProviderFactory(requireContext())
)[CityListViewModel::class.java]

private class MainViewModelProviderFactory(private val context: Context) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val weatherService = WeatherService(context)
        val mainRepository = CityListRepository(weatherService)
        return CityListViewModel(mainRepository) as T
    }
}