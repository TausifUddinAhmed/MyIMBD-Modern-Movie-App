package com.myimbd.data.di

import com.myimbd.data.features.app.DataStoreAppRepository
import com.myimbd.data.features.movielist.MovieDataLocalDataSource
import com.myimbd.data.features.movielist.MovieDataRepositoryImpl
import com.myimbd.data.features.movielist.MoviesDataRemoteDataSource
import com.myimbd.data.features.movielist.local.RoomMovieLocalDataSource
import com.myimbd.data.features.movielist.remote.MovieDataRemoteDataSource
import com.myimbd.domain.features.app.AppRepository
import com.myimbd.domain.features.movielist.MovieListRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    // App
    @Binds
    abstract fun bindAppRepository(dataStoreAppRepository: DataStoreAppRepository): AppRepository

    @Binds
    abstract fun bindMovieDataRepository(movieDataRepositoryImpl: MovieDataRepositoryImpl): MovieListRepository

    @Binds
    abstract fun bindMovieRemoteDataSource(movieDataRemoteDataSource: MovieDataRemoteDataSource): MoviesDataRemoteDataSource

    @Binds
    abstract fun bindMovieLocalDataSource( roomMovieLocalDataSource: RoomMovieLocalDataSource ): MovieDataLocalDataSource

}