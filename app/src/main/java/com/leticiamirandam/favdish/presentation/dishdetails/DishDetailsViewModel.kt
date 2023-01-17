package com.leticiamirandam.favdish.presentation.dishdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.leticiamirandam.favdish.data.cache.mapper.RemoteToCacheMapper
import com.leticiamirandam.favdish.data.cache.room.FavDishRepository
import com.leticiamirandam.favdish.domain.model.FavDish
import kotlinx.coroutines.launch

class DishDetailsViewModel(private val repository: FavDishRepository) : ViewModel() {

    fun update(dish: FavDish) = viewModelScope.launch {
        repository.updateFavDishData(RemoteToCacheMapper().mapFavDishToFavDishCM(dish))
    }
}

class DishDetailsViewModelFactory(private val repository: FavDishRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DishDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DishDetailsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}