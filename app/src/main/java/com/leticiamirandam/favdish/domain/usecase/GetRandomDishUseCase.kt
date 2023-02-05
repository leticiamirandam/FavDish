package com.leticiamirandam.favdish.domain.usecase

import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.domain.repository.RandomDishRepository
import kotlinx.coroutines.flow.Flow

internal class GetRandomDishUseCase(
    private val randomDishRepository: RandomDishRepository,
) {
    operator fun invoke(): Flow<FavDish> =
        randomDishRepository.getRandomDish()
}