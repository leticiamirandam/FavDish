package com.leticiamirandam.favdish.data.repository

import com.leticiamirandam.favdish.data.cache.datasource.alldishes.AllDishesCacheDataSource
import com.leticiamirandam.favdish.data.cache.mapper.CacheToDomainMapper
import com.leticiamirandam.favdish.data.cache.mapper.RemoteToCacheMapper
import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.domain.repository.AllDishesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class AllDishesRepositoryImpl(
    private val allDishesCacheDataSource: AllDishesCacheDataSource,
    private val cacheToDomainMapper: CacheToDomainMapper,
    private val remoteToCacheMapper: RemoteToCacheMapper,
) : AllDishesRepository {
    override fun getAllDishesList(): Flow<List<FavDish>> =
        allDishesCacheDataSource.getAllDishes().map { cacheToDomainMapper.map(it) }

    override fun deleteDish(dish: FavDish): Flow<Int> =
        allDishesCacheDataSource.deleteDish(remoteToCacheMapper.mapFavDishToFavDishCM(dish))

    override fun getFilteredDishesList(filterType: String): Flow<List<FavDish>> =
        allDishesCacheDataSource.getFilteredDishes(filterType).map { cacheToDomainMapper.map(it) }
}