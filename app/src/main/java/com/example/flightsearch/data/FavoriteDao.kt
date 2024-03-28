package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT fav.*, dep.name as departure_name, dest.name as destination_name " +
            "from favorite as fav " +
            "JOIN airport dep ON fav.departure_code = dep.iata_code " +
            "JOIN airport dest ON fav.destination_code = dest.iata_code")
    fun listAllFavorites(): Flow<List<FavoriteData>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavorite(favorite: Favorite)

    @Delete
    suspend fun removeFavorite(favorite: Favorite)
}