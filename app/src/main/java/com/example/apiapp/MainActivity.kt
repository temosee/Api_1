package com.example.apiapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.apiapp.ui.*
import com.example.apiapp.ui.theme.ApiappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ApiappTheme {
                val navController = rememberNavController()
                val viewModel: MainViewModel = viewModel()

                NavHost(navController = navController, startDestination = "list") {
                    composable("list") {
                        CharacterListScreen(
                            state = viewModel.listUiState,
                            searchQuery = viewModel.searchQuery,
                            onSearchChange = { viewModel.onSearchQueryChange(it) },
                            onCharacterClick = { id ->
                                navController.navigate("detail/$id")
                            },
                            onRetry = { viewModel.loadCharacters(viewModel.searchQuery.ifBlank { null }) },
                            onFavoritesClick = {
                                navController.navigate("favorites")
                            }
                        )
                    }
                    composable(
                        route = "detail/{characterId}",
                        arguments = listOf(navArgument("characterId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getInt("characterId") ?: 0
                        
                        LaunchedEffect(id) {
                            viewModel.loadCharacterDetail(id)
                        }

                        CharacterDetailScreen(
                            state = viewModel.detailUiState,
                            isFavorite = viewModel.isFavorite(id),
                            onToggleFavorite = { viewModel.toggleFavorite(it) },
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("favorites") {
                        FavoritesScreen(
                            favorites = viewModel.favorites.toList(),
                            onCharacterClick = { id ->
                                navController.navigate("detail/$id")
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
