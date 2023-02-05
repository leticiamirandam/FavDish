package com.leticiamirandam.favdish.data.cache.datasource.adddish

import com.leticiamirandam.favdish.data.cache.model.FavDishCM
import com.leticiamirandam.favdish.data.cache.room.FavDishDao
import kotlinx.coroutines.flow.flow

class AddDishCacheDataSourceImpl(
    private val favDishDao: FavDishDao,
): AddDishCacheDataSource {
    override fun addDish(favDishCM: FavDishCM) {
        flow { emit(favDishDao.insertFavoriteDishDetails(favDishCM)) }
    }
}