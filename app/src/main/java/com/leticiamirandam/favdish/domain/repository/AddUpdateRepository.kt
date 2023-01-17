package com.leticiamirandam.favdish.domain.repository

import com.leticiamirandam.favdish.data.cache.model.FavDishCM

internal interface AddUpdateRepository {
    fun insertDish(dish: FavDishCM)
    fun updateDish(dish: FavDishCM)
}