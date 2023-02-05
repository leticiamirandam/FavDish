package com.leticiamirandam.favdish.data.datasource.randomdish

import com.leticiamirandam.favdish.data.model.response.RandomDishResponse
import kotlinx.coroutines.flow.Flow

internal interface RandomDishRemoteDataSource {
    fun getRandomDish(): Flow<RandomDishResponse.RecipesListResponse>
}