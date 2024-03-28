package com.example.flightsearch.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.data.Airport
import com.example.flightsearch.data.Favorite
import com.example.flightsearch.data.FavoriteData
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: FlightSearchViewModel = viewModel(factory = FlightSearchViewModel.Factory)
) {
    val airportList by viewModel.getAirportList().collectAsState(initial = listOf())

    Scaffold(modifier = modifier) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                SearchBar(
                    query = viewModel.searchQuery,
                    onQueryChange = viewModel::onQueryChange,
                    onSearch = {},
                    active = viewModel.isSearchBarActive,
                    onActiveChange = viewModel::onSearchBarActive,
                    placeholder = { Text(text = "Enter departure airport") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                viewModel.onQueryChange("")
                                viewModel.onSearchBarActive(false)
                            })
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    if (viewModel.searchQuery.isNotBlank() && viewModel.selectedAirport == null) {
                        LazyColumn() {
                            items(items = airportList) { airport ->
                                ListItem(
                                    headlineContent = {
                                        Text(
                                            text = airport.iataCode,
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    supportingContent = {
                                        Text(
                                            text = airport.name
                                        )
                                    },
                                    modifier = Modifier.clickable {
                                        viewModel.onSelectAirport(
                                            airport
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                // When an airport is selected on the search list
                if (viewModel.selectedAirport !== null) {
                    val arriveAirportList by viewModel.getAllOtherAirportsList()
                        .collectAsState(initial = listOf())

                    FlightsList(
                        departureAirport = viewModel.selectedAirport!!,
                        arriveAirportList = arriveAirportList,
                        viewModel = viewModel
                    )
                } else {
                    val favoriteList by viewModel.getAllFavorites()
                        .collectAsState(initial = listOf())

                    FavoriteList(favoriteList = favoriteList, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun FavoriteList(
    modifier: Modifier = Modifier,
    favoriteList: List<FavoriteData>,
    viewModel: FlightSearchViewModel
) {
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier) {
        Text(
            text = "Favorite routes",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp),
            style = MaterialTheme.typography.labelLarge
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(favoriteList) { favorite ->
                FlightCard(
                    departureAirportName = favorite.departureName,
                    departureAirportCode = favorite.departureCode,
                    arriveAirportName = favorite.destinationName,
                    arriveAirportCode = favorite.destinationCode,
                    onFavoriteClick = {
                        coroutineScope.launch {
                            viewModel.removeFavorite(
                                Favorite(
                                    favorite.id,
                                    favorite.departureCode,
                                    favorite.destinationCode
                                )
                            )
                        }
                    },
                    isFavoriteList = true
                )
            }
        }
    }
}

@Composable
fun FlightsList(
    modifier: Modifier = Modifier,
    departureAirport: Airport,
    arriveAirportList: List<Airport>,
    viewModel: FlightSearchViewModel
) {
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier) {
        Text(
            text = "Flights from ${departureAirport.iataCode}",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp),
            style = MaterialTheme.typography.labelLarge
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(arriveAirportList) { airport ->
                FlightCard(
                    departureAirportCode = departureAirport.iataCode,
                    departureAirportName = departureAirport.name,
                    arriveAirportCode = airport.iataCode,
                    arriveAirportName = airport.name,
                    onFavoriteClick = {
                        coroutineScope.launch {
                            viewModel.addFavorite(
                                departureCode = departureAirport.iataCode,
                                destinationCode = airport.iataCode
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun FlightCard(
    modifier: Modifier = Modifier,
    departureAirportName: String,
    departureAirportCode: String,
    arriveAirportName: String,
    arriveAirportCode: String,
    onFavoriteClick: () -> Unit,
    isFavoriteList: Boolean = false
) {
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(topEnd = 50.dp)) {
        Row {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            ) {
                Text(text = "Depart".uppercase(), style = MaterialTheme.typography.labelMedium)
                Row {
                    Text(text = departureAirportCode, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = departureAirportName, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Arrive".uppercase(), style = MaterialTheme.typography.labelMedium)
                Row {
                    Text(text = arriveAirportCode, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = arriveAirportName, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Icon(
                imageVector = Icons.Default.Star,
                tint = if (isFavoriteList) Color.Yellow else Color.Unspecified,
                contentDescription = "Favorite",
                modifier = Modifier
                    .weight(0.4F)
                    .align(Alignment.CenterVertically)
                    .clickable {
                        onFavoriteClick()
                    }
            )
        }
    }
}

@Preview
@Composable
private fun FlightCardPreview() {
    val airport1 =
        Airport(id = 1, iataCode = "OPO", name = "Fernandópolis Airport", passengers = 100)
    val airport2 = Airport(id = 2, iataCode = "AAA", name = "Araraquara Airport", passengers = 200)
    FlightCard(
        departureAirportCode = airport1.iataCode,
        departureAirportName = airport1.name,
        arriveAirportCode = airport2.iataCode,
        arriveAirportName = airport2.name,
        onFavoriteClick = {})
}

@Preview(showBackground = true)
@Composable
private fun FlightsListPreview() {
    val airport1 =
        Airport(id = 1, iataCode = "OPO", name = "Fernandópolis Airport", passengers = 100)
    val airportList = listOf<Airport>(
        Airport(id = 1, iataCode = "ABC", name = "ABC Airport", passengers = 100),
        Airport(id = 2, iataCode = "DEF", name = "DEF Airport", passengers = 100),
        Airport(id = 3, iataCode = "GHI", name = "GHI Airport", passengers = 100)
    )
    FlightsList(
        departureAirport = airport1,
        arriveAirportList = airportList,
        viewModel = viewModel()
    )
}
