package com.leticiamirandam.favdish.presentation.randomdish

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.leticiamirandam.favdish.data.model.response.RandomDishResponse
import com.leticiamirandam.favdish.data.api.RandomDishApiService
import com.leticiamirandam.favdish.data.cache.mapper.CacheToDomainMapper
import com.leticiamirandam.favdish.data.cache.mapper.RemoteToCacheMapper
import com.leticiamirandam.favdish.data.cache.room.FavDishRepository
import com.leticiamirandam.favdish.domain.model.FavDish
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch

class RandomDishViewModel(private val repository: FavDishRepository) : ViewModel() {

    private val randomRecipeApiService = RandomDishApiService()
    private val compositeDisposable = CompositeDisposable()

    val loadRandomDish = MutableLiveData<Boolean>()
    val randomDishResponse = MutableLiveData<RandomDishResponse.RecipesListResponse>()
    val randomDishLoadingError = MutableLiveData<Boolean>()

    fun insert(dish: FavDish) = viewModelScope.launch {
        repository.insertFavDishData(RemoteToCacheMapper().mapFavDishToFavDishCM(dish))
    }

    fun getRandomRecipeFromApi() {
        loadRandomDish.value = true
        compositeDisposable.add(
            randomRecipeApiService.getRandomDish()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<RandomDishResponse.RecipesListResponse>() {
                    override fun onSuccess(value: RandomDishResponse.RecipesListResponse) {
                        loadRandomDish.value = false
                        randomDishResponse.value = value
                        randomDishLoadingError.value = false
                    }

                    override fun onError(e: Throwable) {
                        loadRandomDish.value = false
                        randomDishLoadingError.value = true
                        e.printStackTrace()
                    }
                })
        )
    }
}

class RandomDishViewModelFactory(private val repository: FavDishRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RandomDishViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RandomDishViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}