package com.leticiamirandam.favdish.presentation.alldishes

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.domain.usecase.DeleteDishUseCase
import com.leticiamirandam.favdish.domain.usecase.GetAllDishesUseCase
import com.leticiamirandam.favdish.domain.usecase.GetFilteredDishListUseCase
import kotlinx.coroutines.launch

internal class AllDishesViewModel(
    private val getAllDishesUseCase: GetAllDishesUseCase,
    private val deleteDishUseCase: DeleteDishUseCase,
    private val getFilteredDishListUseCase: GetFilteredDishListUseCase,
) : ViewModel() {

    val allDishesList: LiveData<List<FavDish>> = getAllDishesUseCase().asLiveData()

    fun delete(dish: FavDish) = viewModelScope.launch {
        deleteDishUseCase(dish)
    }

    fun getFilteredList(value: String): LiveData<List<FavDish>> =
        getFilteredDishListUseCase(value).asLiveData()
}