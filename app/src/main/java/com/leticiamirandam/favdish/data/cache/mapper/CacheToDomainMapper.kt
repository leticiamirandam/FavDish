package com.leticiamirandam.favdish.data.cache.mapper

import com.leticiamirandam.favdish.data.cache.model.FavDishCM
import com.leticiamirandam.favdish.domain.model.FavDish

internal class CacheToDomainMapper {

    fun map(favDishList: List<FavDishCM>) =
        favDishList.map {
            mapFavDishCMToFavDish(it)
        }

    fun mapFavDishCMToFavDish(favDishCM: FavDishCM) = FavDish(
        id = favDishCM.id,
        image = favDishCM.image,
        imageSource = favDishCM.imageSource,
        title = favDishCM.title,
        type = favDishCM.type,
        category = favDishCM.category,
        ingredients = favDishCM.ingredients,
        cookingTime = favDishCM.cookingTime,
        directionToCook = favDishCM.directionToCook,
        favoriteDish = favDishCM.favoriteDish,
    )
}