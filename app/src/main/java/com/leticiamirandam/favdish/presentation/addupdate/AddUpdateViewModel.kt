package com.leticiamirandam.favdish.presentation.addupdate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.domain.usecase.AddDishUseCase
import com.leticiamirandam.favdish.domain.usecase.UpdateDishUseCase
import kotlinx.coroutines.launch

internal class AddUpdateViewModel(
    private val addDishUseCase: AddDishUseCase,
    private val updateDishUseCase: UpdateDishUseCase,
) : ViewModel() {

    fun insert(dish: FavDish) = viewModelScope.launch {
        addDishUseCase(dish)
    }

    fun update(dish: FavDish) = viewModelScope.launch {
        updateDishUseCase(dish)
    }
}