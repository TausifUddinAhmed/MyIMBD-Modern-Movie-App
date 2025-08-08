package com.myimbd.data.di

import android.content.Context
import com.myimbd.data.api.myimdb.MovieApiService
import com.myimbd.domain.exceptions.TemporarilyUnavailableNetworkServiceException
import com.github.davidepanidev.kotlinextensions.minutesBetween
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    private const val MINUTES_TO_WAIT_WHEN_REACHED_MAX_REQUESTS = 1
    private var timeWhenMaxRequestsLimitIsReached: LocalDateTime? = null

    @Provides
    @Singleton
    fun provideCoinGeckoApiService(
        @ApplicationContext context: Context
    ): MovieApiService {

        val cacheSize = 10 * 1024 * 1024L // 10MB
        val cache = Cache(context.cacheDir, cacheSize)

        // Logging interceptor to print request & response info
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Timber.tag("OkHttp").d(message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor) // <-- Added here
            .addNetworkInterceptor { chain ->
                Timber.d("NetworkInterceptor: ${chain.request()}")

                timeWhenMaxRequestsLimitIsReached?.let {
                    val minutesFromLimitReached = it.minutesBetween(LocalDateTime.now())

                    if (minutesFromLimitReached >= MINUTES_TO_WAIT_WHEN_REACHED_MAX_REQUESTS) {
                        resetLimitReachedState()
                    } else {
                        throw TemporarilyUnavailableNetworkServiceException(
                            serviceName = "CoinGecko"
                        )
                    }
                }

                val response = chain.proceed(chain.request())

                Timber.d("${response.headers}")

                when (response.code) {
                    429 -> {
                        setMaxRequestsLimitReached()
                        throw TemporarilyUnavailableNetworkServiceException(
                            serviceName = "CoinGecko"
                        )
                    }
                    else -> response
                }
            }
            .cache(cache)
            .build()

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(MovieApiService.BASE_URL)
            .client(okHttpClient)
            .build()
            .create(MovieApiService::class.java)
    }

    private fun resetLimitReachedState() {
        timeWhenMaxRequestsLimitIsReached = null
    }

    private fun setMaxRequestsLimitReached() {
        timeWhenMaxRequestsLimitIsReached = LocalDateTime.now()
    }
}
