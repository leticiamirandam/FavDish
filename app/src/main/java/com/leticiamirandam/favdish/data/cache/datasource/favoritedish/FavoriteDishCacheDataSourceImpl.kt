package com.leticiamirandam.favdish.data.cache.datasource.favoritedish

import com.leticiamirandam.favdish.data.cache.model.FavDishCM
import com.leticiamirandam.favdish.data.cache.room.FavDishDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class FavoriteDishCacheDataSourceImpl(
    private val favDishDao: FavDishDao,
): FavoriteDishCacheDataSource {
    override fun getFavoriteDishes(): Flow<List<FavDishCM>> = flow {
        emit(favDishDao.geFavoriteDishesList())
    }
}