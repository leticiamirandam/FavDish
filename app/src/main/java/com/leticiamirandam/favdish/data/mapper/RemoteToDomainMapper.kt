package com.leticiamirandam.favdish.data.mapper

import com.leticiamirandam.favdish.data.model.response.RandomDishResponse
import com.leticiamirandam.favdish.domain.model.FavDish
import com.leticiamirandam.favdish.utils.Constants

internal class RemoteToDomainMapper {

    fun mapRecipeResponseToFavDish(recipeResponse: RandomDishResponse.RecipeResponse): FavDish {
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
        return FavDish(
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