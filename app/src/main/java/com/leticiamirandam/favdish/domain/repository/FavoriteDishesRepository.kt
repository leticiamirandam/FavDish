package com.leticiamirandam.favdish.domain.repository

import com.leticiamirandam.favdish.domain.model.FavDish
import kotlinx.coroutines.flow.Flow

internal interface FavoriteDishesRepository {
    fun getFavoriteDishes(): Flow<List<FavDish>>
}