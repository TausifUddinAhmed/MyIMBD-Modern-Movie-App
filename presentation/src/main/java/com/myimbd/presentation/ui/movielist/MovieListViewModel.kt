package com.myimbd.presentation.ui.movielist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.myimbd.domain.features.movielist.MovieListRepository
import com.myimbd.domain.features.movielist.MovieListUseCase
import com.myimbd.domain.models.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val movieListUseCase: MovieListUseCase,
    private val repo: MovieListRepository
) : ViewModel() {

    // get paginated movies from Room; cache in VM scope for config changes
    val movies: Flow<PagingData<Movie>> = movieListUseCase().cachedIn(viewModelScope)

    fun onWishlistClick(movie: Movie) {
        viewModelScope.launch {
            repo.setWishListed(movie.id, !movie.isWishListed)
        }
    }
}
