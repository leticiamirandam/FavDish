package com.leticiamirandam.favdish.domain.repository

import com.leticiamirandam.favdish.domain.model.FavDish

internal interface FavoriteDishesRepository {
    fun getFavoriteDishes(): List<FavDish>
}