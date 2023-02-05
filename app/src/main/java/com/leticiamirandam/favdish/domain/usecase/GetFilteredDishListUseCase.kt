package com.leticiamirandam.favdish.domain.usecase

import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.domain.repository.AllDishesRepository
import kotlinx.coroutines.flow.Flow

internal class GetFilteredDishListUseCase(
    private val allDishesRepository: AllDishesRepository,
) {
    operator fun invoke(filterType: String): Flow<List<FavDish>> =
        allDishesRepository.getFilteredDishesList(filterType)
}