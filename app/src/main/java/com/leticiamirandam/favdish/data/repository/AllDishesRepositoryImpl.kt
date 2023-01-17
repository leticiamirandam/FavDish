package com.leticiamirandam.favdish.data.repository

import com.leticiamirandam.favdish.data.cache.mapper.CacheToDomainMapper
import com.leticiamirandam.favdish.data.cache.mapper.RemoteToCacheMapper
import com.leticiamirandam.favdish.data.cache.model.FavDishCM
import com.leticiamirandam.favdish.data.cache.room.FavDishDao
import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.domain.repository.AllDishesRepository

internal class AllDishesRepositoryImpl(
    private val favDishDao: FavDishDao,
    private val remoteToCacheMapper: RemoteToCacheMapper,
    private val cacheToDomainMapper: CacheToDomainMapper,
): AllDishesRepository {
    override fun getAllDishesList(): List<FavDish> {
        TODO("Not yet implemented")
    }

    override fun deleteDish(dish: FavDishCM) {
        TODO("Not yet implemented")
    }

    override fun getFilteredDishesList(): List<FavDish> {
        TODO("Not yet implemented")
    }

}