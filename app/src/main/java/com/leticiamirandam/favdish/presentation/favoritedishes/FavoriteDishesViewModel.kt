package com.leticiamirandam.favdish.presentation.favoritedishes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.domain.usecase.GetFavoriteDishesUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

internal class FavoriteDishesViewModel(
    private val getFavoriteDishesUseCase: GetFavoriteDishesUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    val loadFavoriteDishes = MutableLiveData<Boolean>()
    val favoriteDishesListResponse = MutableLiveData<List<FavDish>>()
    val favoriteDishesLoadingError = MutableLiveData<Boolean>()

    fun getFavoriteDishes() {
        viewModelScope.launch {
            getFavoriteDishesUseCase()
                .flowOn(dispatcher)
                .onStart { loadFavoriteDishes.value = true }
                .catch {
                    loadFavoriteDishes.value = false
                    favoriteDishesLoadingError.value = true
                    it.printStackTrace()
                }
                .collect {
                    loadFavoriteDishes.value = false
                    favoriteDishesLoadingError.value = false
                    favoriteDishesListResponse.value = it
                }
        }
    }
}