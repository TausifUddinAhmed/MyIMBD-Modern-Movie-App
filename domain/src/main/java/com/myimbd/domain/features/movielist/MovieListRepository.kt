package com.myimbd.domain.features.movielist

import androidx.paging.PagingData
import com.myimbd.domain.models.Movie
import kotlinx.coroutines.flow.Flow

interface MovieListRepository {

    suspend fun getDataFromRemote(): Result<List<Movie>>
    fun getPagedMovies(): Flow<PagingData<Movie>>


}