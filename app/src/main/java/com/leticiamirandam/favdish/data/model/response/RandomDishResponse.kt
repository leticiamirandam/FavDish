package com.leticiamirandam.favdish.data.model.response

object RandomDishResponse {
    data class RecipesListResponse(
        val recipes: List<RecipeResponse>
    )

    data class RecipeResponse(
        val id: Int,
        val image: String,
        val title: String,
        val dishTypes: List<String>,
        val extendedIngredients: List<ExtendedIngredientResponse>,
        val readyInMinutes: Int,
        val instructions: String,
    )

    data class ExtendedIngredientResponse(
        val original: String,
    )
}