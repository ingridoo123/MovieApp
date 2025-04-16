package com.example.movieapp.data.repository

import com.example.movieapp.data.local.media.MediaDao
import com.example.movieapp.data.local.media.MediaEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MyMoviesRepositoryImpl @Inject constructor(private val mediaDao: MediaDao) {

    suspend fun insertMovie(myMovie: MediaEntity) {
        mediaDao.insertMovie(myMovie)
    }

    suspend fun removeFromList(mediaId: Int){
        mediaDao.removeFromList(mediaId)
    }

    suspend fun exist(mediaId: Int): Int {
        return mediaDao.exists(mediaId)
    }

    fun getAllWatchListData(): Flow<List<MediaEntity>> {
        return mediaDao.getAllWatchListData()
    }


}