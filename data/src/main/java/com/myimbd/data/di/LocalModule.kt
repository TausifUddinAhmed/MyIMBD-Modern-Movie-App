package com.myimbd.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.myimbd.data.db.room.MyIMBD
import com.myimbd.data.features.movielist.local.MovieDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")


@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MyIMBD {
        return Room.databaseBuilder(
            context,
            MyIMBD::class.java,
            "movie.db"
        ).build()
    }




    @Provides
    @Singleton
    fun provideMovieDao(
        myIMBD: MyIMBD
    ): MovieDao = myIMBD.movieDao()

}