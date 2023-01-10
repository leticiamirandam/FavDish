package com.leticiamirandam.favdish.application

import android.app.Application
import com.leticiamirandam.favdish.model.database.FavDishRepository
import com.leticiamirandam.favdish.model.database.FavDishRoomDatabase

class FavDishApplication : Application() {

    private val database by lazy { FavDishRoomDatabase.getDatabase(this@FavDishApplication) }

    val repository by lazy { FavDishRepository(database.favDishDao()) }
}