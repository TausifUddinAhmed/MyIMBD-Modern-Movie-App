package com.myimbd.presentation.mappers

import com.myimbd.domain.models.Movie

// Example UI model; adjust to your UI needs
data class MovieUiModel(
    val id: Int,
    val title: String,
    val subtitle: String,   // e.g., "Year • Runtime"
    val posterUrl: String
)

// In UiMapper, add something like:
fun UiMapper.mapMoviesToUi(movies: List<Movie>): List<MovieUiModel> =
    movies.map { m ->
        MovieUiModel(
            id = m.id,
            title = m.title,
            subtitle = "${m.year} • ${m.runtime}",
            posterUrl = m.posterUrl
        )
    }