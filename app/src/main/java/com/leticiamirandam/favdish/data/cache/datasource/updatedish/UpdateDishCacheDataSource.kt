package com.leticiamirandam.favdish.data.cache.datasource.updatedish

import com.leticiamirandam.favdish.data.cache.model.FavDishCM

internal interface UpdateDishCacheDataSource {
    suspend fun updateDish(favDishCM: FavDishCM)
}