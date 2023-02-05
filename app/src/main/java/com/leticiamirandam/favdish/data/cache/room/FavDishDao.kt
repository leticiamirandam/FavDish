package com.leticiamirandam.favdish.data.cache.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.leticiamirandam.favdish.data.cache.model.FavDishCM
import kotlinx.coroutines.flow.Flow

@Dao
interface FavDishDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavoriteDishDetails(favDish: FavDishCM)

    @Query("SELECT * FROM FAV_DISHES_TABLE ORDER BY ID")
    fun getAllDishesList(): Flow<List<FavDishCM>>

    @Update
    suspend fun updateFavDishDetails(favDish: FavDishCM)

    @Query("SELECT * FROM FAV_DISHES_TABLE WHERE favorite_dish = 1")
    fun geFavoriteDishesList(): Flow<List<FavDishCM>>

    @Delete
    suspend fun deleteFavDishDetails(favDish: FavDishCM)

    @Query("SELECT * FROM FAV_DISHES_TABLE WHERE type = :filterType")
    fun getFilteredDishesList(filterType: String): Flow<List<FavDishCM>>
}