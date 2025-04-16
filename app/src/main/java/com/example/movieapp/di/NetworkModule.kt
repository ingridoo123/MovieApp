package com.example.movieapp.di

import android.content.Context
import android.provider.MediaStore.Audio.Media
import androidx.room.Room
import com.example.movieapp.data.local.media.MediaDao
import com.example.movieapp.data.local.media.MediaDatabase
import com.example.movieapp.data.remote.MediaAPI
import com.example.movieapp.data.remote.MediaAPI.Companion.BASE_URL
import com.example.movieapp.data.repository.MyMoviesRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun providesBaseUrl(): String {
        return BASE_URL
    }

    @Provides
    fun providesLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        val okHttpClient = OkHttpClient().newBuilder()

        okHttpClient.callTimeout(40, TimeUnit.SECONDS)
        okHttpClient.connectTimeout(40, TimeUnit.SECONDS)
        okHttpClient.readTimeout(40, TimeUnit.SECONDS)
        okHttpClient.writeTimeout(40, TimeUnit.SECONDS)
        okHttpClient.addInterceptor(loggingInterceptor)
        okHttpClient.build()
        return okHttpClient.build()
    }

    @Provides
    fun provideConverterFactory(): Converter.Factory {
        return GsonConverterFactory.create()
    }

    @Provides
    fun provideRetrofitClient(okHttpClient: OkHttpClient, baseUrl:String, converterFactory: Converter.Factory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()
    }

    @Provides
    fun provideApiService(retrofit: Retrofit): MediaAPI {
        return retrofit.create(MediaAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideLocalDatabase(@ApplicationContext context: Context): MediaDatabase = Room.databaseBuilder(context, MediaDatabase::class.java, "watch_list_table").build()

    @Provides
    fun provideMediaDao(mediaDatabase: MediaDatabase) = mediaDatabase.mediaDao

    @Provides
    @Singleton
    fun provideMyMoviesRepository(mediaDao: MediaDao): MyMoviesRepositoryImpl = MyMoviesRepositoryImpl(mediaDao = mediaDao)


}