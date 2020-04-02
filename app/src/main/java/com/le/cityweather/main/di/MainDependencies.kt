package com.le.cityweather.main.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.le.cityweather.main.repository.MainRepository
import com.le.cityweather.main.ui.MainFragment
import com.le.cityweather.main.vm.MainViewModel
import com.le.weatherapi.WeatherService

fun MainFragment.createMainViewModel(): MainViewModel = ViewModelProvider(
    this,
    MainViewModelProviderFactory(requireContext())
)[MainViewModel::class.java]

private class MainViewModelProviderFactory(private val context: Context) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val weatherService = WeatherService(context)
        val mainRepository = MainRepository(weatherService)
        return MainViewModel(mainRepository) as T
    }
}