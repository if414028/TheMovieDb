package com.reynaldo.themoviedb.di

import com.reynaldo.themoviedb.data.repository.MovieRepositoryImpl
import com.reynaldo.themoviedb.domain.repository.MovieRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMovieRepository(
        implementation: MovieRepositoryImpl
    ): MovieRepository
}