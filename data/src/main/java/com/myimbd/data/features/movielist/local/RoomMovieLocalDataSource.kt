package com.myimbd.data.features.movielist.local


import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.github.davidepanidev.kotlinextensions.utils.dispatchers.DispatcherProvider
import com.myimbd.data.features.movielist.MovieDataLocalDataSource
import com.myimbd.data.features.movielist.local.model.MovieEntity
import com.myimbd.data.mappers.RoomDataMapper
import com.myimbd.domain.models.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoomMovieLocalDataSource @Inject constructor(
    private val movieDao: MovieDao,
    private val mapper: RoomDataMapper,        // Movie <-> MovieEntity
    private val dispatchers: DispatcherProvider
) : MovieDataLocalDataSource {

    override fun getPagedMovies(
        pageSize: Int,
        prefetchDistance: Int,
        enablePlaceholders: Boolean
    ): Flow<PagingData<Movie>> =
        Pager(
            config = PagingConfig(
                pageSize = pageSize,
                prefetchDistance = prefetchDistance,
                enablePlaceholders = enablePlaceholders
            ),
            pagingSourceFactory = { movieDao.pagingSource() }
        ).flow
            .map { paging -> paging.map { entity -> mapper.entityToDomain(entity) } }
            .flowOn(dispatchers.io)




    override suspend fun insertMovies(movies: List<Movie>) = withContext(dispatchers.io) {
        val entities = mapper.domainToEntities(movies)
        movieDao.refreshMovies(entities) // transaction: deleteAll + insert
    }


    override suspend fun setWishListed(movieId: Int, wishListed: Boolean) = withContext(dispatchers.io) {
        movieDao.setWishListed(movieId, wishListed)
    }

    override fun getWishlistedPagedMovies(
        pageSize: Int,
        prefetchDistance: Int,
        enablePlaceholders: Boolean
    ): Flow<PagingData<Movie>> =
        Pager(
            config = PagingConfig(
                pageSize = pageSize,
                prefetchDistance = prefetchDistance,
                enablePlaceholders = enablePlaceholders
            ),
            pagingSourceFactory = { movieDao.wishlistedPagingSource() }
        ).flow
            .map { paging -> paging.map { entity -> mapper.entityToDomain(entity) } }
            .flowOn(dispatchers.io)


}

fun RoomDataMapper.entityToDomain(e: MovieEntity): Movie = Movie(
    id = e.id,
    title = e.title,
    year = e.year,
    runtime = e.runtime,
    genres = e.genres,
    director = e.director,
    actors = e.actors,
    plot = e.plot,
    posterUrl = e.posterUrl,
    isWishListed = e.isWishListed
)