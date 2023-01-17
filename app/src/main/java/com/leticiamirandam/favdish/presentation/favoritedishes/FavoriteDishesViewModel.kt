package com.leticiamirandam.favdish.presentation.favoritedishes

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.leticiamirandam.favdish.data.cache.model.FavDishCM
import com.leticiamirandam.favdish.data.cache.room.FavDishRepository

class FavoriteDishesViewModel(private val repository: FavDishRepository) : ViewModel() {

    val favoriteDishes: LiveData<List<FavDishCM>> = repository.favoriteDishes.asLiveData()
}

class FavoriteDishesViewModelFactory(private val repository: FavDishRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteDishesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoriteDishesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}