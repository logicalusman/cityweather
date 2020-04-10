package com.le.cityweather.main.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.le.utils.Result
import com.le.utils.asNetworkFailure
import com.le.utils.asSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL
import java.util.*


class CountryViewData {

    fun countryName(countryISOCode: String): String =
        if (countryISOCode.isBlank()) "" else Locale("", countryISOCode).displayName

    suspend fun countryFlag(countryISOCode: String): Result<out Bitmap> =
        withContext(Dispatchers.IO) {
            try {
                val url: URL =
                    URL("https://www.countryflags.io/${countryISOCode.toLowerCase(Locale.getDefault())}/flat/64.png")
                return@withContext BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    .asSuccess()
            } catch (e: IOException) {
                return@withContext e.asNetworkFailure()
            }

        }
}
