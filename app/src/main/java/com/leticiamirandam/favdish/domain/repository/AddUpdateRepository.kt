package com.leticiamirandam.favdish.domain.repository

import com.leticiamirandam.favdish.domain.model.FavDish

internal interface AddUpdateRepository {
    suspend fun insertDish(dish: FavDish)
    suspend fun updateDish(dish: FavDish)
}