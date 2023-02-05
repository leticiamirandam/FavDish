package com.leticiamirandam.favdish.data.cache.datasource.adddish

import com.leticiamirandam.favdish.data.cache.model.FavDishCM

internal interface AddDishCacheDataSource {
    fun addDish(favDishCM: FavDishCM)
}