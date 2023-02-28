package com.leticiamirandam.favdish.data.cache.mapper

import com.leticiamirandam.favdish.data.cache.model.FavDishCM
import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.data.model.response.RandomDishResponse
import com.leticiamirandam.favdish.utils.Constants

internal class RemoteToCacheMapper {

    fun mapFavDishToFavDishCM(favDish: FavDish) = FavDishCM(
        id = if (favDish.id == 0) { null } else { favDish.id },
        image = favDish.image,
        imageSource = favDish.imageSource,
        title = favDish.title,
        type = favDish.type,
        category = favDish.category,
        ingredients = favDish.ingredients,
        cookingTime = favDish.cookingTime,
        directionToCook = favDish.directionToCook,
        favoriteDish = favDish.favoriteDish,
    )

    fun mapFavDishToFavDishCMDeleteOperation(favDish: FavDish) = FavDishCM(
        id = favDish.id,
        image = favDish.image,
        imageSource = favDish.imageSource,
        title = favDish.title,
        type = favDish.type,
        category = favDish.category,
        ingredients = favDish.ingredients,
        cookingTime = favDish.cookingTime,
        directionToCook = favDish.directionToCook,
        favoriteDish = favDish.favoriteDish,
    )
}