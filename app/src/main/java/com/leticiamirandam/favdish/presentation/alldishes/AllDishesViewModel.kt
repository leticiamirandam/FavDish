package com.leticiamirandam.favdish.presentation.alldishes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.domain.usecase.DeleteDishUseCase
import com.leticiamirandam.favdish.domain.usecase.GetAllDishesUseCase
import com.leticiamirandam.favdish.domain.usecase.GetFilteredDishListUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

internal class AllDishesViewModel(
    private val getAllDishesUseCase: GetAllDishesUseCase,
    private val deleteDishUseCase: DeleteDishUseCase,
    private val getFilteredDishListUseCase: GetFilteredDishListUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    val loadAllDishes = MutableLiveData<Boolean>()
    val allDishesListResponse = MutableLiveData<List<FavDish>>()
    val deleteDishResponse = MutableLiveData<Boolean>()
    val allDishesLoadingError = MutableLiveData<Boolean>()

    fun getAllDishes() {
        viewModelScope.launch {
            getAllDishesUseCase()
                .flowOn(dispatcher)
                .onStart { loadAllDishes.value = true }
                .catch {
                    loadAllDishes.value = false
                    allDishesLoadingError.value = true
                    it.printStackTrace()
                }
                .collect {
                    loadAllDishes.value = false
                    allDishesLoadingError.value = false
                    allDishesListResponse.value = it
                }
        }
    }

    fun deleteDish(dish: FavDish) {
        viewModelScope.launch {
            deleteDishUseCase(dish)
                .flowOn(dispatcher)
                .onStart { loadAllDishes.value = true }
                .catch {
                    loadAllDishes.value = false
                    allDishesLoadingError.value = true
                    it.printStackTrace()
                }
                .collect {
                    loadAllDishes.value = false
                    allDishesLoadingError.value = false
                    deleteDishResponse.value = it == 1
                }
        }
    }

    fun getFilteredList(value: String) {
        viewModelScope.launch {
            getFilteredDishListUseCase(value)
                .flowOn(dispatcher)
                .onStart { loadAllDishes.value = true }
                .catch {
                    loadAllDishes.value = false
                    allDishesLoadingError.value = true
                    it.printStackTrace()
                }
                .collect {
                    loadAllDishes.value = false
                    allDishesLoadingError.value = false
                    allDishesListResponse.value = it
                }
        }
    }
}