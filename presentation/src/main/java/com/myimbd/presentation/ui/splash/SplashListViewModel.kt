package com.myimbd.presentation.ui.splash


import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myimbd.domain.features.app.IsMovieDataStoreAlreadyShownUseCase
import com.myimbd.domain.features.app.SetMovieDataStoredShownUseCase
import com.myimbd.domain.features.movielist.MovieListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val isMovieDataStoreAlreadyShownUseCase: IsMovieDataStoreAlreadyShownUseCase,
    private  val setMovieDataStoredShownUseCase: SetMovieDataStoredShownUseCase,
    private val movieListUseCase: MovieListUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<SplashUiState>(SplashUiState.Idle)
    val state: StateFlow<SplashUiState> = _state.asStateFlow()

    private var loadJob: Job? = null

    var shouldFetchDataFromRemote by mutableStateOf(false)
        private set



    /** Decide if we need to fetch from remote or not */
    fun decideFetch() {
        viewModelScope.launch {
            val alreadyStored = isMovieDataStoreAlreadyShownUseCase()
            Timber.tag("alreadyStored ").e("alreadyStored  " + alreadyStored)

            shouldFetchDataFromRemote = !alreadyStored
            if (!shouldFetchDataFromRemote) {
                // We can move on immediately
                _state.value = SplashUiState.Success
            }
        }
    }


    fun loadMovies() {
        if (loadJob?.isActive == true) return
        _state.value = SplashUiState.Loading
        loadJob = viewModelScope.launch {
            val result = movieListUseCase(true) // Result<List<Movie>>
            result.fold(
                onSuccess = {
                    setMovieDataStoredShownUseCase()
                    _state.value = SplashUiState.Success
                },
                onFailure = { throwable ->
                    _state.value = SplashUiState.Error(throwable)
                }
            )
        }
    }

    sealed interface SplashUiState {
        data object Idle : SplashUiState
        data object Loading : SplashUiState
        data object Success : SplashUiState
        data class Error(val error: Throwable) : SplashUiState
    }
}
