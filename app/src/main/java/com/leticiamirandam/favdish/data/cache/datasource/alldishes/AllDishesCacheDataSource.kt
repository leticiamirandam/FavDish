package com.leticiamirandam.favdish.data.cache.datasource.alldishes

import com.leticiamirandam.favdish.data.cache.model.FavDishCM
import kotlinx.coroutines.flow.Flow

internal interface AllDishesCacheDataSource {
    fun getAllDishes(): Flow<List<FavDishCM>>
    fun deleteDish(favDishCM: FavDishCM): Flow<Int>
    fun getFilteredDishes(filterType: String): Flow<List<FavDishCM>>
}