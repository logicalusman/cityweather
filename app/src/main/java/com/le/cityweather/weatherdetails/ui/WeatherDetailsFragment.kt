package com.le.cityweather.weatherdetails.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.le.cityweather.R
import com.le.cityweather.domain.WeatherData
import com.le.cityweather.main.MainActivity
import com.le.cityweather.weatherdetails.di.createWeatherDetailsViewModel
import com.le.cityweather.weatherdetails.vm.WeatherDetailsViewModel
import kotlinx.android.synthetic.main.weather_details_fragment.*

class WeatherDetailsFragment() : Fragment() {

    private lateinit var viewModel: WeatherDetailsViewModel
    private val args: WeatherDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.weather_details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = createWeatherDetailsViewModel()
        setupToolbar()
        observeViewStates()
        observeViewActions()
        viewModel.getWeather(args.cityId)
    }

    override fun onStart() {
        super.onStart()
        setupToolbar()
    }

    private fun setupToolbar() {
        rootActivity.apply {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = ""
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.onBackPressed()
                }
            })
    }

    private fun observeViewStates() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is WeatherDetailsViewModel.WeatherDetailsViewState.Loading -> progress.isVisible = it.show
                is WeatherDetailsViewModel.WeatherDetailsViewState.Idle -> {
                    progress.visibility = View.GONE
                    showWeatherData(it.weatherData)
                }
                is WeatherDetailsViewModel.WeatherDetailsViewState.ErrorSnackbar -> showErrorSnackbar(
                    it.errorMessage,
                    it.action
                )
            }
        })
    }

    private fun showErrorSnackbar(errorMessage: String, action: String) {
        Snackbar.make(root, errorMessage, Snackbar.LENGTH_INDEFINITE).apply {
            this.setAction(action) {
                viewModel.onRetry(args.cityId)
            }
        }.show()
    }

    private fun observeViewActions() {
        viewModel.viewAction.observe(viewLifecycleOwner, Observer {
            when (it) {
                is WeatherDetailsViewModel.ViewAction.Finish -> findNavController().navigate(
                    WeatherDetailsFragmentDirections.actionWeatherDetailsFragmentToMainFragment()
                )
            }
        })
    }

    private fun showWeatherData(weatherData: WeatherData) {
        rootActivity.supportActionBar?.title = weatherData.location
        weather_status.text = weatherData.status
        weather_description.text = weatherData.description
    }

}

private val WeatherDetailsFragment.rootActivity get() = requireActivity() as MainActivity
