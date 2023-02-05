package com.leticiamirandam.favdish.data.cache.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.leticiamirandam.favdish.data.cache.model.FavDishCM

@Database(entities = [FavDishCM::class], version = 1)
abstract class FavDishRoomDatabase : RoomDatabase() {
    abstract fun favDishDao(): FavDishDao
}