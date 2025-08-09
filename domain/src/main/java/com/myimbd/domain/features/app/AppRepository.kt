package com.myimbd.domain.features.app

interface AppRepository {

    suspend fun isMovieDataAlreadyStored(): Boolean
    suspend fun setMovieDataStored()

}
