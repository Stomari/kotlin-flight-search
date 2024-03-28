package com.example.flightsearch.data

import kotlinx.coroutines.flow.Flow

interface FlightSearchRepository {
    fun listAirportsStream(search: String): Flow<List<Airport>>

    fun listAllOtherAirportsStream(query: String): Flow<List<Airport>>

    fun listFavoritesStream(): Flow<List<FavoriteData>>

    suspend fun addFavorite(favorite: Favorite)

    suspend fun removeFavorite(favorite: Favorite)
}

class FlightSearchRepo(private val airportDao: AirportDao, private val favoriteDao: FavoriteDao) :
    FlightSearchRepository {
    override fun listAirportsStream(search: String): Flow<List<Airport>> =
        airportDao.listAirports(search)

    override fun listAllOtherAirportsStream(query: String): Flow<List<Airport>> =
        airportDao.listAllOtherAirports(query)

    override fun listFavoritesStream(): Flow<List<FavoriteData>> = favoriteDao.listAllFavorites()

    override suspend fun addFavorite(favorite: Favorite) = favoriteDao.addFavorite(favorite)

    override suspend fun removeFavorite(favorite: Favorite) = favoriteDao.removeFavorite(favorite)
}