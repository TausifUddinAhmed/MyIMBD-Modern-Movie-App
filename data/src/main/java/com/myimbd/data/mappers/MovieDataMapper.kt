package com.myimbd.data.mappers


import com.myimbd.domain.models.*
import com.myimbd.data.api.myimdb.models.MoviesResponseDto
import javax.inject.Inject

class MovieDataMapper @Inject constructor(
) {

    fun mapMovies(response: MoviesResponseDto): List<Movie> {
        return response.movies.map { dto ->
            Movie(
                id = dto.id,
                title = dto.title,
                year = dto.year,
                runtime = dto.runtime,
                genres = dto.genres,
                director = dto.director,
                actors = dto.actors,
                plot = dto.plot,
                posterUrl = dto.posterUrl,
                isWishListed = false
            )
        }
    }






}