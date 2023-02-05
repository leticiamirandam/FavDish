package com.leticiamirandam.favdish.data.api

import com.leticiamirandam.favdish.data.model.response.RandomDishResponse
import com.leticiamirandam.favdish.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Query

interface RandomDishAPI {

    @GET("recipes/random")
    suspend fun getRandomDish(
        @Query(Constants.API_KEY) apiKey: String = Constants.API_KEY_VALUE,
        @Query(Constants.LIMIT_LICENSE) limitLicense: Boolean = Constants.LIMIT_LICENSE_VALUE,
        @Query(Constants.TAGS) tags: String = Constants.TAGS_VALUE,
        @Query(Constants.NUMBER) number: Int = Constants.NUMBER_VALUE,
    ): RandomDishResponse.RecipesListResponse
}