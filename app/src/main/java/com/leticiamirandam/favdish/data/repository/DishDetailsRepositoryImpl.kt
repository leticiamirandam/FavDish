package com.leticiamirandam.favdish.data.repository

import com.leticiamirandam.favdish.data.cache.mapper.CacheToDomainMapper
import com.leticiamirandam.favdish.data.cache.mapper.RemoteToCacheMapper
import com.leticiamirandam.favdish.data.cache.model.FavDishCM
import com.leticiamirandam.favdish.data.cache.room.FavDishDao
import com.leticiamirandam.favdish.domain.repository.DishDetailsRepository

internal class DishDetailsRepositoryImpl(
    private val favDishDao: FavDishDao,
    private val cacheToDomainMapper: CacheToDomainMapper,
    private val remoteToCacheMapper: RemoteToCacheMapper,
): DishDetailsRepository {
    override fun updateDish(dishCM: FavDishCM) {
        TODO("Not yet implemented")
    }
}