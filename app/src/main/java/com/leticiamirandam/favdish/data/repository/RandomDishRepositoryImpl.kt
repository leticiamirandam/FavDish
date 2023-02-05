package com.leticiamirandam.favdish.data.repository

import com.leticiamirandam.favdish.data.cache.datasource.adddish.AddDishCacheDataSource
import com.leticiamirandam.favdish.data.cache.mapper.RemoteToCacheMapper
import com.leticiamirandam.favdish.data.datasource.randomdish.RandomDishRemoteDataSource
import com.leticiamirandam.favdish.data.mapper.RemoteToDomainMapper
import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.domain.repository.RandomDishRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

internal class RandomDishRepositoryImpl(
    private val randomDishRemoteDataSource: RandomDishRemoteDataSource,
    private val addDishCacheDataSource: AddDishCacheDataSource,
    private val remoteToDomainMapper: RemoteToDomainMapper,
    private val remoteToCacheMapper: RemoteToCacheMapper,
) : RandomDishRepository {
    override fun getRandomDish(): Flow<FavDish> =
        randomDishRemoteDataSource.getRandomDish()
            .map { remoteToDomainMapper.mapRecipeResponseToFavDish(it.recipes[0]) }


    override fun insertDish(favDish: FavDish) {
        flow { emit(addDishCacheDataSource.addDish(remoteToCacheMapper.mapFavDishToFavDishCM(favDish))) }
    }
}