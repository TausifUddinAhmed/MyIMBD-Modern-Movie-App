package com.myimbd.presentation.ui.splash


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myimbd.domain.features.movielist.MovieListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val movieListUseCase: MovieListUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<SplashUiState>(SplashUiState.Idle)
    val state: StateFlow<SplashUiState> = _state.asStateFlow()

    private var loadJob: Job? = null

    fun loadMovies() {
        if (loadJob?.isActive == true) return
        _state.value = SplashUiState.Loading
        loadJob = viewModelScope.launch {
            val result = movieListUseCase(true) // Result<List<Movie>>
            result.fold(
                onSuccess = {
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
