package com.leticiamirandam.favdish.domain.repository

import com.leticiamirandam.favdish.domain.model.FavDish
import kotlinx.coroutines.flow.Flow

internal interface AllDishesRepository {
    fun getAllDishesList(): Flow<List<FavDish>>
    fun deleteDish(dish: FavDish): Flow<Int>
    fun getFilteredDishesList(filterType: String): Flow<List<FavDish>>
}