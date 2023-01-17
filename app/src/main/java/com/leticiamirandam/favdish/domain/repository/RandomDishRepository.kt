package com.leticiamirandam.favdish.domain.repository

import com.leticiamirandam.favdish.data.model.response.RandomDishResponse

internal interface RandomDishRepository {
    fun getRandomDish(): RandomDishResponse
    fun insertDish()
}