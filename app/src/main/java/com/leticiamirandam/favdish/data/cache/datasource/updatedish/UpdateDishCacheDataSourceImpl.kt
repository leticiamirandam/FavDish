package com.leticiamirandam.favdish.data.cache.datasource.updatedish

import com.leticiamirandam.favdish.data.cache.model.FavDishCM
import com.leticiamirandam.favdish.data.cache.room.FavDishDao
import kotlinx.coroutines.flow.flow

internal class UpdateDishCacheDataSourceImpl(
    private val favDishDao: FavDishDao,
) : UpdateDishCacheDataSource {
    override suspend fun updateDish(favDishCM: FavDishCM) {
        favDishDao.updateFavDishDetails(favDishCM)
    }
}