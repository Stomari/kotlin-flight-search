package com.example.flightsearch

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.flightsearch.data.AppContainer
import com.example.flightsearch.data.DataStoreRepository
import com.example.flightsearch.data.DefaultAppContainer

private const val PREFERENCES_NAME = "user_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)

class FlightSearchApp : Application() {
    lateinit var container: AppContainer
    lateinit var dataStoreRepository: DataStoreRepository

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        dataStoreRepository = DataStoreRepository(dataStore)
    }
}