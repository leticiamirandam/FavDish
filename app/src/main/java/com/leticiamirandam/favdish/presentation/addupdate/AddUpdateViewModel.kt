package com.leticiamirandam.favdish.presentation.addupdate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.leticiamirandam.favdish.data.cache.mapper.CacheToDomainMapper
import com.leticiamirandam.favdish.data.cache.mapper.RemoteToCacheMapper
import com.leticiamirandam.favdish.data.cache.room.FavDishRepository
import com.leticiamirandam.favdish.domain.model.FavDish
import kotlinx.coroutines.launch

class AddUpdateViewModel(private val repository: FavDishRepository) : ViewModel()  {

    fun insert(dish: FavDish) = viewModelScope.launch {
        repository.insertFavDishData(RemoteToCacheMapper().mapFavDishToFavDishCM(dish))
    }

    fun update(dish: FavDish) = viewModelScope.launch {
        repository.updateFavDishData(RemoteToCacheMapper().mapFavDishToFavDishCM(dish))
    }
}

class AddUpdateViewModelFactory(private val repository: FavDishRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddUpdateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddUpdateViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}