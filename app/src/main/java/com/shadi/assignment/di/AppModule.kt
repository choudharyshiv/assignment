package com.shadi.assignment.di

import android.app.Application
import androidx.room.Room
import com.shadi.assignment.data.local.AppDatabase
import com.shadi.assignment.data.local.UserProfileDao
import com.shadi.assignment.data.remote.MatchApiService
import com.shadi.assignment.data.repository.MatchRepository
import com.shadi.assignment.domain.usecase.AcceptMatchUseCase
import com.shadi.assignment.domain.usecase.DeclineMatchUseCase
import com.shadi.assignment.domain.usecase.GetMatchesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase =
        Room.databaseBuilder(app, AppDatabase::class.java, app.getString(com.shadi.assignment.R.string.db_name)).build()

    @Provides
    fun provideUserProfileDao(db: AppDatabase): UserProfileDao = db.userProfileDao()

    @Provides
    @Singleton
    fun provideRetrofit(app: Application): Retrofit =
        Retrofit.Builder()
            .baseUrl(app.getString(com.shadi.assignment.R.string.api_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): MatchApiService =
        retrofit.create(MatchApiService::class.java)

    @Provides
    @Singleton
    fun provideRepository(api: MatchApiService, dao: UserProfileDao): MatchRepository =
        MatchRepository(api, dao)

    @Provides
    fun provideGetMatchesUseCase(repository: MatchRepository): GetMatchesUseCase =
        GetMatchesUseCase(repository)

    @Provides
    fun provideAcceptMatchUseCase(repository: MatchRepository): AcceptMatchUseCase =
        AcceptMatchUseCase(repository)

    @Provides
    fun provideDeclineMatchUseCase(repository: MatchRepository): DeclineMatchUseCase =
        DeclineMatchUseCase(repository)
}

