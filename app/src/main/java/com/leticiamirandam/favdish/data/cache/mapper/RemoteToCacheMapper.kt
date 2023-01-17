package com.leticiamirandam.favdish.data.cache.mapper

import com.leticiamirandam.favdish.data.cache.model.FavDishCM
import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.data.model.response.RandomDishResponse
import com.leticiamirandam.favdish.utils.Constants

internal class RemoteToCacheMapper {

    fun mapFavDishToFavDishCM(favDish: FavDish) = FavDishCM(
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

    fun mapRecipeResponseToFavDishCM(recipeResponse: RandomDishResponse.RecipeResponse): FavDishCM {
        var dishType: String = "other"
        if (recipeResponse.dishTypes.isNotEmpty()) {
            dishType = recipeResponse.dishTypes[0]
        }
        var ingredients = ""
        for (value in recipeResponse.extendedIngredients) {
            ingredients = if (ingredients.isEmpty()) {
                value.original
            } else {
                ingredients + ", \n" + value.original
            }
        }
        return FavDishCM(
            id = recipeResponse.id,
            image = recipeResponse.image,
            imageSource = Constants.DISH_IMAGE_SOURCE_ONLINE,
            title = recipeResponse.title,
            type = dishType,
            category = "Other",
            ingredients = ingredients,
            cookingTime = recipeResponse.readyInMinutes.toString(),
            directionToCook = recipeResponse.instructions,
            favoriteDish = true,
        )
    }
}