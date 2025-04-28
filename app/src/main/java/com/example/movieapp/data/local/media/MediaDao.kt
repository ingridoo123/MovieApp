package com.example.movieapp.data.local.media


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertMovie(mediaEntity: MediaEntity)

    @Query("DELETE FROM watch_list_table WHERE mediaId = :mediaId")
    suspend fun removeFromList(mediaId: Int)

    @Query("SELECT EXISTS (SELECT 1 FROM watch_list_table WHERE mediaId = :mediaId)")
    suspend fun exists(mediaId: Int):Int

    @Query("SELECT * FROM watch_list_table ORDER BY addedOn DESC")
    fun getAllMoviesData(): Flow<List<MediaEntity>>

}