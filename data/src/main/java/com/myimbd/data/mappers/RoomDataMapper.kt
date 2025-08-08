package com.myimbd.data.mappers


import com.github.davidepanidev.kotlinextensions.utils.serialization.SerializationManager
import com.myimbd.data.features.movielist.local.model.MovieEntity
import com.myimbd.domain.models.Movie
import javax.inject.Inject

class RoomDataMapper @Inject constructor(
    private val serializationManager: SerializationManager
) {


    fun domainToEntities(movies: List<Movie>): List<MovieEntity> =
        movies.mapIndexed { index, m ->
            MovieEntity(
                sortedPosition = index,           // or keep existing order logic
                id = m.id,
                title = m.title,
                year = m.year,                    // if String; if Int, adjust type
                runtime = m.runtime,
                genres = m.genres,
                director = m.director,
                actors = m.actors,
                plot = m.plot,
                posterUrl = m.posterUrl
            )
        }

    fun entitiesToDomain(entities: List<MovieEntity>): List<Movie> =
        entities.map { e ->
            Movie(
                id = e.id,
                title = e.title,
                year = e.year,
                runtime = e.runtime,
                genres = e.genres,
                director = e.director,
                actors = e.actors,
                plot = e.plot,
                posterUrl = e.posterUrl
            )
        }

}