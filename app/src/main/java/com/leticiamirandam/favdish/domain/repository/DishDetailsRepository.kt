package com.leticiamirandam.favdish.domain.repository

import com.leticiamirandam.favdish.data.cache.model.FavDishCM

internal interface DishDetailsRepository {
    fun updateDish(dishCM: FavDishCM)
}