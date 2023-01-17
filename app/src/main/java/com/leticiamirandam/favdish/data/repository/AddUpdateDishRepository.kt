package com.leticiamirandam.favdish.data.repository

import com.leticiamirandam.favdish.data.api.RandomDishApiService
import com.leticiamirandam.favdish.data.cache.mapper.CacheToDomainMapper
import com.leticiamirandam.favdish.data.cache.mapper.RemoteToCacheMapper
import com.leticiamirandam.favdish.data.cache.room.FavDishDao
import com.leticiamirandam.favdish.data.model.response.RandomDishResponse
import com.leticiamirandam.favdish.domain.repository.RandomDishRepository

internal class AddUpdateDishRepository(
    private val service: RandomDishApiService,
    private val favDishDao: FavDishDao,
    private val cacheToDomainMapper: CacheToDomainMapper,
    private val remoteToCacheMapper: RemoteToCacheMapper,
): RandomDishRepository {
    override fun getRandomDish(): RandomDishResponse {
        TODO("Not yet implemented")
    }

    override fun insertDish() {
        TODO("Not yet implemented")
    }
}