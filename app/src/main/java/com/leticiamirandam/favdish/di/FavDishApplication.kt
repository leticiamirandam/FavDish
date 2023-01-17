package com.leticiamirandam.favdish.di

import android.app.Application
import com.leticiamirandam.favdish.data.cache.room.FavDishRepository
import com.leticiamirandam.favdish.data.cache.room.FavDishRoomDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FavDishApplication : Application() {

    private val database by lazy { FavDishRoomDatabase.getDatabase(this@FavDishApplication) }

    val repository by lazy { FavDishRepository(database.favDishDao()) }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@FavDishApplication)
            modules(
                listOf(
                    networkModule, favDishNetworkModule
                )
            )
        }
    }
}