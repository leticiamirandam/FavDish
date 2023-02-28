package com.leticiamirandam.favdish.data.cache.datasource.alldishes

import com.leticiamirandam.favdish.data.cache.model.FavDishCM
import com.leticiamirandam.favdish.data.cache.room.FavDishDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class AllDishesCacheDataSourceImpl(
    private val favDishDao: FavDishDao,
) : AllDishesCacheDataSource {
    override fun getAllDishes(): Flow<List<FavDishCM>> = flow {
        emit(favDishDao.getAllDishesList())
    }

    override fun deleteDish(favDishCM: FavDishCM): Flow<Int> = flow {
        emit(favDishDao.deleteFavDishDetails(favDishCM))
    }

    override fun getFilteredDishes(filterType: String): Flow<List<FavDishCM>> = flow {
        emit(favDishDao.getFilteredDishesList(filterType))
    }
}