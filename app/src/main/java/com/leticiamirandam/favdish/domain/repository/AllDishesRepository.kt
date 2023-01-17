package com.leticiamirandam.favdish.domain.repository

import com.leticiamirandam.favdish.data.cache.model.FavDishCM
import com.leticiamirandam.favdish.domain.model.FavDish

internal interface AllDishesRepository {
    fun getAllDishesList(): List<FavDish>
    fun deleteDish(dish: FavDishCM)
    fun getFilteredDishesList(): List<FavDish>
}