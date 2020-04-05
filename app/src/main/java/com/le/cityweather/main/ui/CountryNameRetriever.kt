package com.le.cityweather.main.ui

import java.util.*

class CountryNameRetriever {

    fun countryName(countryISOCode: String): String =
        if (countryISOCode.isBlank()) "" else Locale("", countryISOCode).displayName
}