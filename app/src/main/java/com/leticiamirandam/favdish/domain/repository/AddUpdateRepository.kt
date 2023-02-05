package com.leticiamirandam.favdish.domain.repository

import com.leticiamirandam.favdish.domain.model.FavDish

internal interface AddUpdateRepository {
    fun insertDish(dish: FavDish)
    fun updateDish(dish: FavDish)
}