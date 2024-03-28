package com.example.flightsearch.data

import android.content.Context

interface AppContainer {
    val flightSearchRepository: FlightSearchRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val flightSearchRepository: FlightSearchRepository by lazy {
        FlightSearchRepo(
            FlightSearchDatabase.getDatabase(context).airportDao(),
            FlightSearchDatabase.getDatabase(context).favoriteDao()
        )
    }
}