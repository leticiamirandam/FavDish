package com.leticiamirandam.favdish.data.cache.room

import androidx.annotation.WorkerThread
import com.leticiamirandam.favdish.data.cache.model.FavDishCM

class FavDishRepository(private val favDishDao: FavDishDao) {

    @WorkerThread
    suspend fun updateFavDishData(favDish: FavDishCM) {
        favDishDao.updateFavDishDetails(favDish)
    }

    @WorkerThread
    suspend fun deleteFavDishData(favDish: FavDishCM) {
        favDishDao.deleteFavDishDetails(favDish)
    }
}