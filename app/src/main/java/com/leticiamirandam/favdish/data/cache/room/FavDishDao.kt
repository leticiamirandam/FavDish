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

    @Query("SELECT * FROM FAV_DISHES ORDER BY ID")
    suspend fun getAllDishesList(): List<FavDishCM>

    @Update
    suspend fun updateFavDishDetails(favDish: FavDishCM)

    @Query("SELECT * FROM FAV_DISHES WHERE favorite_dish = 1")
    fun geFavoriteDishesList(): List<FavDishCM>

    @Delete
    suspend fun deleteFavDishDetails(favDish: FavDishCM) : Int

    @Query("SELECT * FROM FAV_DISHES WHERE type = :filterType")
    suspend fun getFilteredDishesList(filterType: String): List<FavDishCM>
}