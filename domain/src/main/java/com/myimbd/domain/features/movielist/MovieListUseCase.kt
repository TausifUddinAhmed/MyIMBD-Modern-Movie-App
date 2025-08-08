package com.myimbd.domain.features.movielist

import androidx.paging.PagingData
import com.myimbd.domain.models.Movie
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject



class MovieListUseCase @Inject constructor(
    private val movieListRepository: MovieListRepository
) {

    suspend operator fun invoke(refresh: Boolean): Result<List<Movie>> {
        return if (refresh) {
            movieListRepository.getDataFromRemote()
        } else {
            Result.success(emptyList()) // or load cached data
        }
    }

    operator fun invoke(): Flow<PagingData<Movie>> {
        return movieListRepository.getPagedMovies()
    }
}
