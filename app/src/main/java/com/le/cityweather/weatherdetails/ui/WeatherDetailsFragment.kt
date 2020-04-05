package com.le.cityweather.weatherdetails.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.le.cityweather.R
import com.le.cityweather.weatherdetails.di.createWeatherDetailsViewModel
import com.le.cityweather.weatherdetails.vm.WeatherDetailsViewModel

const val EXTRA_CITY_ID = "com.le.cityweather.city_id"

class WeatherDetailsFragment : Fragment() {

    private lateinit var viewModel: WeatherDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.weather_details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = createWeatherDetailsViewModel()
    }

}
