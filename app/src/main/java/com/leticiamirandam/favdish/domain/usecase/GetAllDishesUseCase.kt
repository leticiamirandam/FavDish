package com.leticiamirandam.favdish.domain.usecase

import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.domain.repository.AllDishesRepository
import kotlinx.coroutines.flow.Flow

internal class GetAllDishesUseCase(
    private val allDishesRepository: AllDishesRepository,
) {
    operator fun invoke(): Flow<List<FavDish>> =
        allDishesRepository.getAllDishesList()
}