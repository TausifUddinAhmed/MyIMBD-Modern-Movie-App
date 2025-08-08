package com.myimbd.data.api.myimdb


import com.myimbd.data.api.myimdb.models.MoviesResponseDto
import retrofit2.http.GET

interface MovieApiService {


    @GET("db.json")
    suspend fun getMovies(): MoviesResponseDto

    companion object {
        const val BASE_URL = "https://raw.githubusercontent.com/erik-sytnyk/movies-list/master/"
    }


}