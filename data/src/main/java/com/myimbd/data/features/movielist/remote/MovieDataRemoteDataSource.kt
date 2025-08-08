package com.myimbd.data.features.movielist.remote

import com.myimbd.data.api.myimdb.MovieApiService
import com.github.davidepanidev.kotlinextensions.utils.dispatchers.DispatcherProvider
import com.myimbd.data.features.movielist.MoviesDataRemoteDataSource
import com.myimbd.data.mappers.MovieDataMapper
import com.myimbd.domain.models.Movie
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MovieDataRemoteDataSource @Inject constructor(
    private val api: MovieApiService,
    private val mapper: MovieDataMapper,
    private val dispatchers: DispatcherProvider
) : MoviesDataRemoteDataSource {

    override suspend fun retrieveMovieData(): List<Movie> = withContext(dispatchers.io) {
        val dto = api.getMovies()                 // MoviesResponseDto
        mapper.mapMovies(dto)                     // -> List<Movie>
    }
}
