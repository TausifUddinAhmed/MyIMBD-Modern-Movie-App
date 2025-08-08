package com.myimbd.data.features.movielist.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.myimbd.data.features.movielist.local.model.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MovieDao {

    // reverse chronological by year, then keep stable list order
    @Query("""
        SELECT * FROM movies
        ORDER BY CAST(year AS INTEGER) DESC, sortedPosition ASC
    """)
    abstract fun pagingSource(): PagingSource<Int, MovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertMovies(movies: List<MovieEntity>)

    @Query("DELETE FROM movies")
    abstract suspend fun deleteAllMovies()

    @Transaction
    open suspend fun refreshMovies(movies: List<MovieEntity>) {
        deleteAllMovies()
        insertMovies(movies)
    }
}

