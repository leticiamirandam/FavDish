package com.leticiamirandam.favdish.di

import android.app.Application
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.leticiamirandam.favdish.data.api.RandomDishAPI
import com.leticiamirandam.favdish.data.cache.datasource.adddish.AddDishCacheDataSource
import com.leticiamirandam.favdish.data.cache.datasource.adddish.AddDishCacheDataSourceImpl
import com.leticiamirandam.favdish.data.cache.datasource.alldishes.AllDishesCacheDataSource
import com.leticiamirandam.favdish.data.cache.datasource.alldishes.AllDishesCacheDataSourceImpl
import com.leticiamirandam.favdish.data.cache.datasource.favoritedish.FavoriteDishCacheDataSource
import com.leticiamirandam.favdish.data.cache.datasource.favoritedish.FavoriteDishCacheDataSourceImpl
import com.leticiamirandam.favdish.data.cache.datasource.updatedish.UpdateDishCacheDataSource
import com.leticiamirandam.favdish.data.cache.datasource.updatedish.UpdateDishCacheDataSourceImpl
import com.leticiamirandam.favdish.data.cache.mapper.CacheToDomainMapper
import com.leticiamirandam.favdish.data.cache.mapper.RemoteToCacheMapper
import com.leticiamirandam.favdish.data.cache.room.FavDishDao
import com.leticiamirandam.favdish.data.cache.room.FavDishRoomDatabase
import com.leticiamirandam.favdish.data.datasource.randomdish.RandomDishRemoteDataSource
import com.leticiamirandam.favdish.data.datasource.randomdish.RandomDishRemoteDataSourceImpl
import com.leticiamirandam.favdish.data.mapper.RemoteToDomainMapper
import com.leticiamirandam.favdish.data.repository.AddUpdateRepositoryImpl
import com.leticiamirandam.favdish.data.repository.AllDishesRepositoryImpl
import com.leticiamirandam.favdish.data.repository.FavoriteDishRepositoryImpl
import com.leticiamirandam.favdish.data.repository.RandomDishRepositoryImpl
import com.leticiamirandam.favdish.domain.repository.AddUpdateRepository
import com.leticiamirandam.favdish.domain.repository.AllDishesRepository
import com.leticiamirandam.favdish.domain.repository.FavoriteDishesRepository
import com.leticiamirandam.favdish.domain.repository.RandomDishRepository
import com.leticiamirandam.favdish.domain.usecase.AddDishUseCase
import com.leticiamirandam.favdish.domain.usecase.DeleteDishUseCase
import com.leticiamirandam.favdish.domain.usecase.GetAllDishesUseCase
import com.leticiamirandam.favdish.domain.usecase.GetFavoriteDishesUseCase
import com.leticiamirandam.favdish.domain.usecase.GetFilteredDishListUseCase
import com.leticiamirandam.favdish.domain.usecase.GetRandomDishUseCase
import com.leticiamirandam.favdish.domain.usecase.UpdateDishUseCase
import com.leticiamirandam.favdish.presentation.addupdate.AddUpdateViewModel
import com.leticiamirandam.favdish.presentation.alldishes.AllDishesViewModel
import com.leticiamirandam.favdish.presentation.dishdetails.DishDetailsViewModel
import com.leticiamirandam.favdish.presentation.favoritedishes.FavoriteDishesViewModel
import com.leticiamirandam.favdish.presentation.randomdish.RandomDishViewModel
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://api.spoonacular.com/"
private const val DB_NAME = "fav_dish_database"

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
    single<Retrofit> {
        get<Retrofit.Builder>()
            .baseUrl(BASE_URL)
            .build()
    }
    single<RandomDishAPI> {
        get<Retrofit>().create(RandomDishAPI::class.java)
    }
}

val cacheModule = module {
    fun provideDataBase(application: Application): FavDishRoomDatabase {
        return Room.databaseBuilder(application, FavDishRoomDatabase::class.java, DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    fun provideDao(dataBase: FavDishRoomDatabase): FavDishDao {
        return dataBase.favDishDao()
    }
    single { provideDataBase(androidApplication()) }
    single { provideDao(get()) }
}

val domainModule = module {
    factory { AddDishUseCase(get()) }
    factory { DeleteDishUseCase(get()) }
    factory { GetAllDishesUseCase(get()) }
    factory { GetFavoriteDishesUseCase(get()) }
    factory { GetFilteredDishListUseCase(get()) }
    factory { UpdateDishUseCase(get()) }
    factory { GetRandomDishUseCase(get()) }
}

val dataModule = module {
    factory<AddDishCacheDataSource> { AddDishCacheDataSourceImpl(get()) }
    factory<AllDishesCacheDataSource> { AllDishesCacheDataSourceImpl(get()) }
    factory<FavoriteDishCacheDataSource> { FavoriteDishCacheDataSourceImpl(get()) }
    factory<UpdateDishCacheDataSource> { UpdateDishCacheDataSourceImpl(get()) }
    factory<RandomDishRemoteDataSource> { RandomDishRemoteDataSourceImpl(get()) }

    factory<AddUpdateRepository> { AddUpdateRepositoryImpl(get(), get(), RemoteToCacheMapper()) }
    factory<AllDishesRepository> {
        AllDishesRepositoryImpl(
            get(),
            CacheToDomainMapper(),
            RemoteToCacheMapper()
        )
    }
    factory<FavoriteDishesRepository> { FavoriteDishRepositoryImpl(get(), CacheToDomainMapper()) }
    factory<RandomDishRepository> {
        RandomDishRepositoryImpl(
            get(),
            get(),
            RemoteToDomainMapper(),
            RemoteToCacheMapper()
        )
    }
}

val presentationModule = module {
    viewModel {
        AddUpdateViewModel(
            addDishUseCase = get(),
            updateDishUseCase = get(),
        )
    }

    viewModel {
        AllDishesViewModel(
            getAllDishesUseCase = get(),
            deleteDishUseCase = get(),
            getFilteredDishListUseCase = get(),
        )
    }

    viewModel {
        DishDetailsViewModel(
            updateDishUseCase = get(),
        )
    }

    viewModel {
        FavoriteDishesViewModel(
            getFavoriteDishesUseCase = get(),
        )
    }

    viewModel {
        RandomDishViewModel(
            getRandomDishUseCase = get(),
            updateDishUseCase = get(),
        )
    }
}