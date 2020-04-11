package com.le.cityweather.weatherdetails.di


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.le.utils.ResourceProvider
import com.le.cityweather.weatherdetails.repository.WeatherDetailsRepository
import com.le.cityweather.weatherdetails.ui.WeatherDetailsFragment
import com.le.cityweather.weatherdetails.vm.WeatherDetailsViewModel
import com.le.weatherapi.WeatherService

fun WeatherDetailsFragment.createWeatherDetailsViewModel(): WeatherDetailsViewModel =
    ViewModelProvider(
        this,
        WeatherDetailsViewModelProviderFactory(requireContext())
    )[WeatherDetailsViewModel::class.java]

private class WeatherDetailsViewModelProviderFactory(private val context: Context) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val weatherService = WeatherService(context)
        val repository = WeatherDetailsRepository(weatherService)
        return WeatherDetailsViewModel(repository, ResourceProvider(context)) as T
    }
}
