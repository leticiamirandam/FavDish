package com.leticiamirandam.favdish.presentation.randomdish

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.domain.usecase.AddDishUseCase
import com.leticiamirandam.favdish.domain.usecase.GetRandomDishUseCase
import com.leticiamirandam.favdish.utils.Constants
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

internal class RandomDishViewModel(
    private val getRandomDishUseCase: GetRandomDishUseCase,
    private val addDishUseCase: AddDishUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    val loadRandomDish = MutableLiveData<Boolean>()
    val randomDishResponse = MutableLiveData<FavDish>()
    val randomDishLoadingError = MutableLiveData<Boolean>()

    init {
        getRandomRecipeFromApi()
    }

    fun insert(dish: FavDish) = viewModelScope.launch { addDishUseCase(dish) }

    fun getRandomRecipeFromApi() {
        viewModelScope.launch {
            getRandomDishUseCase()
                .flowOn(dispatcher)
                .onStart { loadRandomDish.value = true }
                .catch {
                    loadRandomDish.value = false
                    randomDishLoadingError.value = true
                    it.printStackTrace()
                }
                .collect {
                    loadRandomDish.value = false
                    randomDishResponse.value = it
                    randomDishLoadingError.value = false
                }
        }
    }

    fun saveFavoriteDish(favDish: FavDish) {
        val randomDishDetais = FavDish(
            id = favDish.id,
            image = favDish.image,
            imageSource = Constants.DISH_IMAGE_SOURCE_ONLINE,
            title = favDish.title,
            type = favDish.type,
            category = "Other",
            ingredients = favDish.ingredients,
            cookingTime = favDish.cookingTime,
            directionToCook = favDish.directionToCook,
            favoriteDish = true
        )
        insert(randomDishDetais)
    }
}