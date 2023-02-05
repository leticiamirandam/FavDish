package com.leticiamirandam.favdish.data.cache.datasource.favoritedish

import com.leticiamirandam.favdish.data.cache.model.FavDishCM
import kotlinx.coroutines.flow.Flow

internal interface FavoriteDishCacheDataSource {
    fun getFavoriteDishes(): Flow<List<FavDishCM>>
}