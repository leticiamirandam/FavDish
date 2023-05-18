package com.leticiamirandam.favdish.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FavDishApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@FavDishApplication)
            modules(
                listOf(
                    networkModule,
                    cacheModule,
                    domainModule,
                    dataModule,
                    presentationModule
                )
            )
        }
    }
}