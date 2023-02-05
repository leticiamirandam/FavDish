package com.leticiamirandam.favdish.domain.usecase

import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.domain.repository.FavoriteDishesRepository
import kotlinx.coroutines.flow.Flow

internal class GetFavoriteDishesUseCase(
    private val favoriteDishesRepository: FavoriteDishesRepository,
) {
    operator fun invoke(): Flow<List<FavDish>> =
        favoriteDishesRepository.getFavoriteDishes()
}