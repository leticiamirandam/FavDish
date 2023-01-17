package com.leticiamirandam.favdish.data.cache.room

import androidx.annotation.WorkerThread
import com.leticiamirandam.favdish.data.cache.model.FavDishCM
import kotlinx.coroutines.flow.Flow

class FavDishRepository(private val favDishDao: FavDishDao) {

    @WorkerThread
    suspend fun insertFavDishData(favDish: FavDishCM) {
        favDishDao.insertFavoriteDishDetails(favDish)
    }

    val allDishesList: Flow<List<FavDishCM>> = favDishDao.getAllDishesList()

    @WorkerThread
    suspend fun updateFavDishData(favDish: FavDishCM) {
        favDishDao.updateFavDishDetails(favDish)
    }

    val favoriteDishes: Flow<List<FavDishCM>> = favDishDao.geFavoriteDishesList()

    @WorkerThread
    suspend fun deleteFavDishData(favDish: FavDishCM) {
        favDishDao.deleteFavDishDetails(favDish)
    }

    fun filteredListDishes(value: String): Flow<List<FavDishCM>> =
        favDishDao.getFilteredDishesList(value)
}