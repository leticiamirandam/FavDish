package com.leticiamirandam.favdish.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FavDish(
    val id: Int?,
    val image: String,
    val imageSource: String,
    val title: String,
    val type: String,
    val category: String,
    val ingredients: String,
    val cookingTime: String,
    val directionToCook: String,
    var favoriteDish: Boolean = false,
): Parcelable