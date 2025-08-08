package com.myimbd.data.api.myimdb.models


data class MoviesResponseDto(
    val genres: List<String>,
    val movies: List<MovieDto>
)

data class MovieDto(
    val id: Int,
    val title: String,
    val year: Int,
    val runtime: String,
    val genres: List<String>,
    val director: String,
    val actors: String,
    val plot: String,
    val posterUrl: String
)
