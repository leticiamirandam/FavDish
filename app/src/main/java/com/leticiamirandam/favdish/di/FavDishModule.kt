package com.leticiamirandam.favdish.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.leticiamirandam.favdish.data.api.RandomDishAPI
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://api.spoonacular.com/"

val networkModule = module {
    single<Gson> { GsonBuilder().create() }
    single {
        OkHttpClient.Builder()
            .build()
    }
    single<GsonConverterFactory> { GsonConverterFactory.create(get()) }
    single<RxJava2CallAdapterFactory> {
        RxJava2CallAdapterFactory.create()
    }
    single<Retrofit.Builder> {
        Retrofit.Builder()
            .client(get())
            .addConverterFactory(get<GsonConverterFactory>())
            .addCallAdapterFactory(get<RxJava2CallAdapterFactory>())
    }
}

val favDishNetworkModule = module {
    single<Retrofit> {
        get<Retrofit.Builder>()
            .baseUrl(BASE_URL)
            .build()
    }
    single<RandomDishAPI> {
        get<Retrofit>().create(RandomDishAPI::class.java)
    }
}