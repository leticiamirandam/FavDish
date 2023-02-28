package com.leticiamirandam.favdish.domain.usecase

import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.domain.repository.AddUpdateRepository

internal class UpdateDishUseCase(
    private val addUpdateRepository: AddUpdateRepository,
) {
    suspend operator fun invoke(favDish: FavDish) {
        addUpdateRepository.updateDish(favDish)
    }
}