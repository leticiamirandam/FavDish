package com.leticiamirandam.favdish.domain.repository

import com.leticiamirandam.favdish.domain.model.FavDish
import kotlinx.coroutines.flow.Flow

internal interface RandomDishRepository {
    fun getRandomDish(): Flow<FavDish>
    fun insertDish(favDish: FavDish)
}