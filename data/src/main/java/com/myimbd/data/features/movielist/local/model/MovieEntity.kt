package com.myimbd.data.features.movielist.local.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "movies",
    indices = [Index(value = ["id"], unique = true)]
)
data class MovieEntity(
    @PrimaryKey val sortedPosition: Int, // Position in list if needed
    val id: Int,
    val title: String,
    val year: Int,
    val runtime: String,
    val genres: List<String>, // Requires TypeConverter
    val director: String,
    val actors: String,
    val plot: String,
    val posterUrl: String
)