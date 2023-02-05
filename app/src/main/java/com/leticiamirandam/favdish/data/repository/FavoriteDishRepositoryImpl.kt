package com.leticiamirandam.favdish.data.repository

import com.leticiamirandam.favdish.data.cache.datasource.favoritedish.FavoriteDishCacheDataSource
import com.leticiamirandam.favdish.data.cache.mapper.CacheToDomainMapper
import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.domain.repository.FavoriteDishesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class FavoriteDishRepositoryImpl(
    private val favoriteDishCacheDataSource: FavoriteDishCacheDataSource,
    private val cacheToDomainMapper: CacheToDomainMapper,
): FavoriteDishesRepository {
    override fun getFavoriteDishes(): Flow<List<FavDish>> =
        favoriteDishCacheDataSource.getFavoriteDishes().map { cacheToDomainMapper.map(it) }
}