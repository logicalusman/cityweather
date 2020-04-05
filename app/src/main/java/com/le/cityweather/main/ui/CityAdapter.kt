package com.le.cityweather.main.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.le.cityweather.R
import com.le.cityweather.model.CityData

class CityAdapter(private val onCityClicked: (CityData) -> Unit) :
    RecyclerView.Adapter<CityViewHolder>() {

    private var cityList = listOf<CityData>()

    fun update(cityList: List<CityData>) {
        this.cityList = cityList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder =
        CityViewHolder(
            itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.item_city,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = cityList.size

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(cityList[position], onCityClicked)
    }
}


class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val cityNameView: TextView = itemView.findViewById(R.id.city_name)
    private val separatorView: View = itemView.findViewById(R.id.separator)
    private val stateView: TextView = itemView.findViewById(R.id.state)
    private val countryView: TextView = itemView.findViewById(R.id.country)


    fun bind(cityData: CityData, onCityClicked: (CityData) -> Unit) {
        cityNameView.text = cityData.name
        if (cityData.state.isNotEmpty()) {
            stateView.visibility = View.VISIBLE
            separatorView.visibility = View.VISIBLE
            stateView.text = cityData.state
        } else {
            stateView.visibility = View.GONE
            separatorView.visibility = View.GONE
        }
        countryView.text = cityData.countryName
        itemView.setOnClickListener {
            onCityClicked(cityData)
        }
    }

}