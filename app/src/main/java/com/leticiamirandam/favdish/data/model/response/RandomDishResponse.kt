package com.leticiamirandam.favdish.data.model.response

import com.google.gson.annotations.SerializedName

data class RandomDishResponse(
    val recipesListResponse: RecipesListResponse
) {

    data class RecipesListResponse(
        @SerializedName("recipes") val recipes: List<RecipeResponse>
    )

    data class RecipeResponse(
        @SerializedName("id") val id: Int,
        @SerializedName("image") val image: String,
        @SerializedName("title") val title: String,
        @SerializedName("dishTypes") val dishTypes: List<String>,
        @SerializedName("extendedIngredients") val extendedIngredients: List<ExtendedIngredientResponse>,
        @SerializedName("readyInMinutes") val readyInMinutes: Int,
        @SerializedName("instructions") val instructions: String,
    )

    data class ExtendedIngredientResponse(
        @SerializedName("original") val original: String,
    )
}

