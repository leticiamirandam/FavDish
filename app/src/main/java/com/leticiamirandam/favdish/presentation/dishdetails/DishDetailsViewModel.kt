package com.leticiamirandam.favdish.presentation.dishdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.domain.usecase.UpdateDishUseCase
import kotlinx.coroutines.launch

internal class DishDetailsViewModel(
    private val updateDishUseCase: UpdateDishUseCase,
) : ViewModel() {

    fun update(dish: FavDish) = viewModelScope.launch {
        updateDishUseCase(dish)
    }
}