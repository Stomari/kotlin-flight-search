package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {
    @Query("SELECT * from airport " +
            "WHERE name LIKE '%' || :value || '%' " +
            "OR iata_code LIKE '%' || :value || '%' " +
            "ORDER BY passengers DESC")
    fun listAirports(value: String): Flow<List<Airport>>

    @Query("SELECT * from airport " +
            "WHERE iata_code IS NOT :query " +
            "ORDER BY passengers DESC")
    fun listAllOtherAirports (query: String):  Flow<List<Airport>>
}