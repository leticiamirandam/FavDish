package com.leticiamirandam.favdish.data.datasource.randomdish

import com.leticiamirandam.favdish.data.api.RandomDishAPI
import com.leticiamirandam.favdish.data.model.response.RandomDishResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class RandomDishRemoteDataSourceImpl(
    private val service: RandomDishAPI,
): RandomDishRemoteDataSource {
    override fun getRandomDish(): Flow<RandomDishResponse.RecipesListResponse> = flow {
        emit(service.getRandomDish())
    }
}