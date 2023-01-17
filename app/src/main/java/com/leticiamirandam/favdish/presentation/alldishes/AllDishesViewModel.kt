package com.leticiamirandam.favdish.presentation.alldishes

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.leticiamirandam.favdish.data.cache.model.FavDishCM
import com.leticiamirandam.favdish.data.cache.room.FavDishRepository
import kotlinx.coroutines.launch

class AllDishesViewModel(private val repository: FavDishRepository) : ViewModel() {

    val allDishesList: LiveData<List<FavDishCM>> = repository.allDishesList.asLiveData()

    fun delete(dish: FavDishCM) = viewModelScope.launch {
        repository.deleteFavDishData(dish)
    }

    fun getFilteredList(value: String): LiveData<List<FavDishCM>> =
        repository.filteredListDishes(value).asLiveData()
}

class AllDishesViewModelFactory(private val repository: FavDishRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AllDishesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AllDishesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}