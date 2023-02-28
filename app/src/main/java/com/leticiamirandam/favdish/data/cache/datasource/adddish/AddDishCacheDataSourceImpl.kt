package com.leticiamirandam.favdish.data.cache.datasource.adddish

import com.leticiamirandam.favdish.data.cache.model.FavDishCM
import com.leticiamirandam.favdish.data.cache.room.FavDishDao

class AddDishCacheDataSourceImpl(
    private val favDishDao: FavDishDao,
): AddDishCacheDataSource {
    override suspend fun addDish(favDishCM: FavDishCM) {
        favDishDao.insertFavoriteDishDetails(favDishCM)
    }
}