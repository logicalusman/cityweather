package com.le.cityweather.citylist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.le.cityweather.R
import com.le.cityweather.citylist.di.createMainViewModel
import com.le.cityweather.citylist.vm.CityListViewModel
import com.le.cityweather.citylist.vm.CityListViewModel.CityListViewState.Idle
import com.le.cityweather.citylist.vm.CityListViewModel.CityListViewState.Loading
import com.le.cityweather.domain.CityData
import kotlinx.android.synthetic.main.city_list_fragment.*


class CityListFragment : Fragment() {

    private lateinit var viewModel: CityListViewModel
    private lateinit var cityAdapter: CityAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.city_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = createMainViewModel()
        setupCityListAdapter()
        observeViewStates()
        observeViewActions()
        observeSearch()
        viewModel.getCityList()
    }

    private fun observeSearch() {
        search_city_input.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.searchCity(text.toString())
        }
    }

    private fun setupCityListAdapter() {
        cityAdapter = CityAdapter(lifecycleScope) {
            viewModel.onCityClicked(it)
        }
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

    private fun observeViewActions() {
        viewModel.viewAction.observe(viewLifecycleOwner, Observer {
            when (it) {
                is CityListViewModel.Action.CityClicked -> {
                    findNavController().navigate(
                        CityListFragmentDirections.actionMainFragmentToWeatherDetailsFragment(it.cityId)
                    )
                }
            }
        })
    }

    private fun showData(cityList: List<CityData>) {
        cityAdapter.update(cityList)
    }


}
