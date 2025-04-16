package com.example.movieapp.data.local.media

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MediaEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MediaDatabase: RoomDatabase() {
    abstract val mediaDao: MediaDao

}