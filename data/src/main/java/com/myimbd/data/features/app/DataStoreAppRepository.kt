package com.myimbd.data.features.app

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.myimbd.domain.features.app.AppRepository
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

class DataStoreAppRepository @Inject constructor(
    private val preferencesDataStore: DataStore<Preferences>
) : AppRepository {

    private val isMovieDataAlreadyStored = booleanPreferencesKey("IS_MOVIE_DATA_STORED")


    override suspend fun isMovieDataAlreadyStored(): Boolean {
        return try {
            preferencesDataStore.data.first()[isMovieDataAlreadyStored] ?: false
        } catch (e: Exception) {
            Timber.e("isAppFirstLaunch ERROR: $e")
            true
        }
    }

    override suspend fun setMovieDataStored() {
        preferencesDataStore.edit {
            it[isMovieDataAlreadyStored] = true
        }
    }


}