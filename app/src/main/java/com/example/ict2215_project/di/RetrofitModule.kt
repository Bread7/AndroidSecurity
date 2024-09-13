package com.example.ict2215_project.di

import com.example.ict2215_project.data.network.StreamChatServerAPI
import com.example.ict2215_project.data.repository.StreamChatServerRepository
import com.example.ict2215_project.domain.repository.IStreamChatServerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://us-central1-ict2215-project.cloudfunctions.net/tasks/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideStreamChatTokenAPI(retrofit: Retrofit): StreamChatServerAPI {
        return retrofit.create(StreamChatServerAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideStreamChatTokenRepository(api: StreamChatServerAPI): IStreamChatServerRepository {
        return StreamChatServerRepository(api)
    }

}