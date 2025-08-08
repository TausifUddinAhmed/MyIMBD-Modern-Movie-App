package com.myimbd.domain.features.movie_details

import com.myimbd.domain.features.movielist.MovieListRepository
import com.myimbd.domain.models.Movie
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMovieDetailsUseCase @Inject constructor(
    private val repo: MovieListRepository
) {
    operator fun invoke(movieId: Int): Flow<Movie?> = repo.getMovieById(movieId)
}