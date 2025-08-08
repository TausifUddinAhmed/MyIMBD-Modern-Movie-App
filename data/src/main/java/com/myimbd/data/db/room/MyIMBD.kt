package com.myimbd.data.db.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.myimbd.data.features.movielist.local.MovieDao
import com.myimbd.data.features.movielist.local.model.MovieEntity


@Database(
    entities = [
        MovieEntity::class
    ],
    version = 5,
    exportSchema = true
//    autoMigrations = [
//        AutoMigration (from = 1, to = 2),
//        AutoMigration (from = 2, to = 3)
//    ]
)
@TypeConverters(Converters::class)
abstract class MyIMBD : RoomDatabase() {

    abstract  fun movieDao(): MovieDao

}