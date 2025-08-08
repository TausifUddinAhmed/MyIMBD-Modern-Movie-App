package com.myimbd.presentation.ui.movie_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myimbd.domain.features.movie_details.GetMovieDetailsUseCase
import com.myimbd.domain.features.movielist.MovieListRepository
import com.myimbd.domain.models.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val getMovieDetails: GetMovieDetailsUseCase,
    private val repo: MovieListRepository
) : ViewModel() {

    fun movie(movieId: Int): Flow<Movie?> = getMovieDetails(movieId)

    fun toggleWishlist(movie: Movie) = viewModelScope.launch {
        repo.setWishListed(movie.id, !movie.isWishListed)
    }
}
