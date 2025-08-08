package com.myimbd.presentation.ui.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.myimbd.domain.features.movielist.MovieListRepository
import com.myimbd.domain.models.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val repo: MovieListRepository
) : ViewModel() {

    // Paged, wishlisted-only data
    val wishlist: Flow<PagingData<Movie>> =
        repo.getWishlistedPagedMovies().cachedIn(viewModelScope)

    fun onWishlistClick(movie: Movie) {
        viewModelScope.launch {
            repo.setWishListed(movie.id, !movie.isWishListed)
        }
    }
}
