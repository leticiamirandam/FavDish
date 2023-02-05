package com.leticiamirandam.favdish.data.repository

import com.leticiamirandam.favdish.data.cache.datasource.adddish.AddDishCacheDataSource
import com.leticiamirandam.favdish.data.cache.datasource.updatedish.UpdateDishCacheDataSource
import com.leticiamirandam.favdish.data.cache.mapper.RemoteToCacheMapper
import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.domain.repository.AddUpdateRepository

internal class AddUpdateRepositoryImpl(
    private val addDishCacheDataSource: AddDishCacheDataSource,
    private val updateCacheDataSource: UpdateDishCacheDataSource,
    private val remoteToCacheMapper: RemoteToCacheMapper,
): AddUpdateRepository {
    override fun insertDish(dish: FavDish) {
        addDishCacheDataSource.addDish(remoteToCacheMapper.mapFavDishToFavDishCM(dish))
    }

    override fun updateDish(dish: FavDish) {
        updateCacheDataSource.updateDish(remoteToCacheMapper.mapFavDishToFavDishCM(dish))
    }
}