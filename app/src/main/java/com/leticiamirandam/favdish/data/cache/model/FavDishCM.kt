package com.leticiamirandam.favdish.data.cache.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fav_dishes_table")
data class FavDishCM(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "image") val image: String,
    @ColumnInfo(name = "image_source") val imageSource: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "ingredients") val ingredients: String,
    @ColumnInfo(name = "cooking_time") val cookingTime: String,
    @ColumnInfo(name = "instructions") val directionToCook: String,
    @ColumnInfo(name = "favorite_dish") val favoriteDish: Boolean,
)