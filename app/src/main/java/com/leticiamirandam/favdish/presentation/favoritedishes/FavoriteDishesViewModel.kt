package com.leticiamirandam.favdish.presentation.favoritedishes

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.domain.usecase.GetFavoriteDishesUseCase

internal class FavoriteDishesViewModel(
    private val getFavoriteDishesUseCase: GetFavoriteDishesUseCase,
) : ViewModel() {

    val favoriteDishes: LiveData<List<FavDish>> = getFavoriteDishesUseCase().asLiveData()
}