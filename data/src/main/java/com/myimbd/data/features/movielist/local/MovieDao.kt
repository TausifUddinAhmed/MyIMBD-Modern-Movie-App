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

    //  set or toggle
    @Query("UPDATE movies SET isWishListed = :wishListed WHERE id = :id")
    abstract suspend fun setWishListed(id: Int, wishListed: Boolean)

    //  toggle version if you want it
    @Query("UPDATE movies SET isWishListed = CASE WHEN isWishListed = 1 THEN 0 ELSE 1 END WHERE id = :id")
    abstract suspend fun toggleWishListed(id: Int)

    @Query("SELECT * FROM movies WHERE isWishlisted = 1 ORDER BY year DESC, id ASC")
    abstract fun wishlistedPagingSource(): PagingSource<Int, MovieEntity>

    @Query("SELECT * FROM movies WHERE id = :id LIMIT 1")
    abstract fun observeById(id: Int): kotlinx.coroutines.flow.Flow<MovieEntity?>


}

