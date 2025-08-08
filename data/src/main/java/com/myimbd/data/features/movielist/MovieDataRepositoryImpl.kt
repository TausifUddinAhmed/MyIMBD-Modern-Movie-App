package com.myimbd.data.features.movielist

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.myimbd.domain.features.movielist.MovieListRepository
import com.myimbd.domain.models.Movie
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MovieDataRepositoryImpl @Inject constructor(
    private val remoteSource: MoviesDataRemoteDataSource,
    private val localSource: MovieDataLocalDataSource
) : MovieListRepository {

    override suspend fun getDataFromRemote(): Result<List<Movie>> = runCatching {
        val movies = remoteSource.retrieveMovieData()
        localSource.insertMovies(movies)   // persist to Room
        movies                              // return fresh data
    }

    override fun getPagedMovies(): Flow<PagingData<Movie>> {
        return localSource.getPagedMovies()
    }

    override suspend fun setWishListed(movieId: Int, wishListed: Boolean) =
        localSource.setWishListed(movieId, wishListed)

    override fun getWishlistedPagedMovies(): Flow<PagingData<Movie>> =
        localSource.getWishlistedPagedMovies()


}




interface MovieDataLocalDataSource {

    fun getPagedMovies(
        pageSize: Int = 10,
        prefetchDistance: Int = 2,
        enablePlaceholders: Boolean = false
    ): Flow<PagingData<Movie>>

    suspend fun insertMovies(movies: List<Movie>)

    suspend fun setWishListed(movieId: Int, wishListed: Boolean) // ← NEW

    /** Only wishlisted movies (paged) */
    fun getWishlistedPagedMovies(
        pageSize: Int = 10,
        prefetchDistance: Int = 2,
        enablePlaceholders: Boolean = false
    ): Flow<PagingData<Movie>>
}


interface MoviesDataRemoteDataSource {


    suspend fun retrieveMovieData(
    ): List<Movie>



}