package com.example.flightsearch.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearch.FlightSearchApp
import com.example.flightsearch.data.Airport
import com.example.flightsearch.data.DataStoreRepository
import com.example.flightsearch.data.Favorite
import com.example.flightsearch.data.FavoriteData
import com.example.flightsearch.data.FlightSearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FlightSearchViewModel(
    private val flightSearchRepository: FlightSearchRepository,
    private val dataStoreRepository: DataStoreRepository
) :
    ViewModel() {
    var searchQuery by mutableStateOf("")
        private set
    var isSearchBarActive by mutableStateOf(false)
        private set
    var selectedAirport: Airport? by mutableStateOf(null)
        private set

    fun onQueryChange(query: String) {
        searchQuery = query
        if (query.isBlank()) {
            selectedAirport = null
            isSearchBarActive = false
        }
        viewModelScope.launch {
            dataStoreRepository.saveSearchQuery(query)
        }
    }

    init {
        fetchStoredSearchQuery()
    }

    fun onSearchBarActive(active: Boolean) {
        isSearchBarActive = active
    }

    fun onSelectAirport(airport: Airport) {
        selectedAirport = airport
        isSearchBarActive = false
    }

    fun getAirportList(): Flow<List<Airport>> =
        flightSearchRepository.listAirportsStream(searchQuery)

    fun getAllOtherAirportsList(): Flow<List<Airport>> =
        flightSearchRepository.listAllOtherAirportsStream(selectedAirport?.iataCode ?: "")

    fun getAllFavorites(): Flow<List<FavoriteData>> =
        flightSearchRepository.listFavoritesStream()

    suspend fun addFavorite(departureCode: String, destinationCode: String) {
        flightSearchRepository.addFavorite(
            Favorite(
                departureCode = departureCode,
                destinationCode = destinationCode
            )
        )
    }

    suspend fun removeFavorite(favorite: Favorite) {
        flightSearchRepository.removeFavorite(favorite)
    }

    private fun fetchStoredSearchQuery() {
        viewModelScope.launch {
            searchQuery = dataStoreRepository.storedSearchQuery.first()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FlightSearchApp)
                FlightSearchViewModel(application.container.flightSearchRepository, application.dataStoreRepository)
            }
        }
    }
}