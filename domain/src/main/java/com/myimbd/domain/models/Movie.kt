package com.myimbd.domain.models



data class Movie(
    val id: Int,
    val title: String,
    val year: Int,
    val runtime: String,
    val genres: List<String>,
    val director: String,
    val actors: String,
    val plot: String,
    val posterUrl: String,
    val isWishListed: Boolean

)