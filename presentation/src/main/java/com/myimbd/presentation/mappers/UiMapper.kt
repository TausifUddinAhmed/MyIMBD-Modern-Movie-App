package com.myimbd.presentation.mappers

import android.os.Build
import androidx.compose.ui.graphics.Color
import com.myimbd.domain.exceptions.TemporarilyUnavailableNetworkServiceException
import com.myimbd.presentation.BuildConfig
import com.myimbd.presentation.models.*
import com.myimbd.presentation.theme.NegativeTrend
import com.myimbd.presentation.theme.PositiveTrend
import com.github.davidepanidev.kotlinextensions.*
import com.github.davidepanidev.kotlinextensions.utils.currencyformatter.CurrencyFormatter
import com.github.davidepanidev.kotlinextensions.utils.numberformatter.NumberFormatter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.annotations.VisibleForTesting
import timber.log.Timber
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class UiMapper @Inject constructor(
    private val currencyFormatter: CurrencyFormatter,
    private val numberFormatter: NumberFormatter,
) {


    fun mapErrorToUiMessage(error: Throwable): String {
        Timber.e("ERROR: $error \n ${error.printStackTrace()}")

        return when(error) {
            is SocketTimeoutException -> "The service is temporarily unavailable. Please try again later."

            is TemporarilyUnavailableNetworkServiceException -> "The ${error.serviceName} service is now at capacity. Please try again in a minute."

            is UnknownHostException,
            is SocketException,
            is IOException -> {
                "You appear to be offline. Please, check your internet connection and retry."
            }

            is retrofit2.HttpException -> {
                when(error.code()) {
                    429, 503 -> "The CoinGecko service is temporarily unavailable [HTTP ${error.code()}]. Please try again later."
                    in 500..599 -> "The CoinGecko server is not responding [HTTP ${error.code()}]. Please try again later."
                    else -> if (BuildConfig.DEBUG) {
                        error.toString()
                    } else {
                        "There has been an error while retrieving data [HTTP ${error.code()}]. Please try again later."
                    }
                }
            }

            else -> if (BuildConfig.DEBUG) {
                error.toString()
            } else {
                "There has been an error while retrieving data. Please try again later."
            }
        }
    }

    private fun Double.correspondingTrendColor(): Color {
        return if (this >= 0) PositiveTrend else NegativeTrend
    }



    private fun Double.roundTo2DecimalsIfTooLong(): Double {
        return if (this >= 1.1) {
            this.roundToNDecimals(decimals = 2)
        } else {
            this
        }
    }

    private fun String?.orNotAvailable(): String {
        return this ?: NOT_AVAILABLE
    }

    private fun String?.orNA(): String {
        return this ?: NA
    }

    private fun Color?.orNeutral(): Color {
        return this ?: Color.Gray
    }


    companion object {
        const val NOT_AVAILABLE = "Not available"
        const val NA = "N.A."
    }

}