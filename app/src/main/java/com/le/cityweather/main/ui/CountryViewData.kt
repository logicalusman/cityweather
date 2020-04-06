package com.le.cityweather.main.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import java.util.*


class CountryViewData {

    suspend fun countryData(countryISOCode: String): Country = withContext(Dispatchers.IO) {
        val countryName =
            if (countryISOCode.isBlank()) "" else Locale("", countryISOCode).displayName
        val url: URL =
            URL("https://www.countryflags.io/${countryISOCode.toLowerCase(Locale.getDefault())}/flat/64.png")
        val countryBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
        Country(countryName, countryBitmap)
    }
}

data class Country(val displayName: String, val flagBitmap: Bitmap)