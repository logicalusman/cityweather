package com.le.weatherapi

data class City(val id: Int, val name: String, val state: String, val countryCode: String)

data class Weather(val locationName: String, val status: String, val description: String)