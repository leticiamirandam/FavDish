package com.leticiamirandam.favdish.data.repository

import com.leticiamirandam.favdish.data.cache.mapper.CacheToDomainMapper
import com.leticiamirandam.favdish.data.cache.mapper.RemoteToCacheMapper
import com.leticiamirandam.favdish.data.cache.room.FavDishDao
import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.domain.repository.FavoriteDishesRepository

internal class FavoriteDishRepositoryImpl(
    private val favDishDao: FavDishDao,
    private val cacheToDomainMapper: CacheToDomainMapper,
    private val remoteToCacheMapper: RemoteToCacheMapper,
): FavoriteDishesRepository {
    override fun getFavoriteDishes(): List<FavDish> {
        TODO("Not yet implemented")
    }
}