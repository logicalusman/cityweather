package com.le.cityweather.main.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.le.cityweather.R
import com.le.cityweather.main.di.createMainViewModel
import com.le.cityweather.main.vm.MainViewModel
import com.le.cityweather.main.vm.MainViewModel.MainViewState.Idle
import com.le.cityweather.main.vm.MainViewModel.MainViewState.Loading
import com.le.cityweather.model.CityData
import kotlinx.android.synthetic.main.main_fragment.*


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private val cityAdapter = CityAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = createMainViewModel()
        setupCityListAdapter()
        observeViewStates()
        observeSearch()
        viewModel.getCityList()
    }

    private fun observeSearch() {
        search_city_input.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.searchCity(text.toString())
        }
    }

    private fun setupCityListAdapter() {
        city_list.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    LinearLayout.VERTICAL
                )
            )
            adapter = cityAdapter
        }
    }

    private fun observeViewStates() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Loading -> progress.visibility = View.VISIBLE
                is Idle -> {
                    progress.visibility = View.INVISIBLE
                    showData(it.data)
                }
            }
        })
    }

    private fun showData(cityList: List<CityData>) {
        cityAdapter.update(cityList)
    }


}
