package com.example.apiapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.apiapp.data.model.Character

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListScreen(
    state: ListUiState,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onCharacterClick: (Int) -> Unit,
    onRetry: () -> Unit,
    onFavoritesClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rick & Morty") },
                actions = {
                    IconButton(onClick = onFavoritesClick) {
                        Icon(Icons.Default.Favorite, contentDescription = "Favorites")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search characters...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )

            when (state) {
                is ListUiState.Loading -> LoadingView()
                is ListUiState.Success -> {
                    LazyColumn {
                        items(state.characters) { character ->
                            CharacterItem(character, onCharacterClick)
                        }
                    }
                }
                is ListUiState.Empty -> ErrorView("No characters found", onRetry)
                is ListUiState.Error -> ErrorView(state.message, onRetry)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    state: DetailUiState,
    isFavorite: Boolean,
    onToggleFavorite: (Character) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        when (state) {
            is DetailUiState.Loading -> LoadingView()
            is DetailUiState.Error -> ErrorView(state.message, onBack)
            is DetailUiState.Success -> {
                val character = state.character
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = character.image,
                        contentDescription = null,
                        modifier = Modifier.size(200.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = character.name, style = MaterialTheme.typography.headlineMedium)
                    Text(text = "Status: ${character.status}")
                    Text(text = "Species: ${character.species}")
                    Text(text = "Gender: ${character.gender}")
                    Text(text = "Origin: ${character.origin.name}")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { onToggleFavorite(character) }) {
                        Icon(
                            if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isFavorite) "Remove from Favorites" else "Add to Favorites")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    favorites: List<Character>,
    onCharacterClick: (Int) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        if (favorites.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No favorites yet")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(favorites) { character ->
                    CharacterItem(character, onCharacterClick)
                }
            }
        }
    }
}

@Composable
fun CharacterItem(character: Character, onClick: (Int) -> Unit) {
    ListItem(
        modifier = Modifier.clickable { onClick(character.id) },
        headlineContent = { Text(character.name) },
        supportingContent = { Text(character.species) },
        leadingContent = {
            AsyncImage(
                model = character.image,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                contentScale = ContentScale.Crop
            )
        }
    )
}

@Composable
fun LoadingView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}
