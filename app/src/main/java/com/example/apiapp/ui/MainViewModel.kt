package com.example.apiapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apiapp.data.model.Character
import com.example.apiapp.data.repository.RickAndMortyRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed interface ListUiState {
    object Loading : ListUiState
    data class Success(val characters: List<Character>) : ListUiState
    data class Error(val message: String) : ListUiState
    object Empty : ListUiState
}

sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(val character: Character) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

class MainViewModel : ViewModel() {
    private val repository = RickAndMortyRepository()

    var listUiState: ListUiState by mutableStateOf(ListUiState.Loading)
        private set

    var detailUiState: DetailUiState by mutableStateOf(DetailUiState.Loading)
        private set

    var searchQuery by mutableStateOf("")
        private set

    var favorites by mutableStateOf(setOf<Character>())
        private set

    private var searchJob: Job? = null

    init {
        loadCharacters()
    }

    fun loadCharacters(query: String? = null) {
        viewModelScope.launch {
            listUiState = ListUiState.Loading
            try {
                val results = repository.getCharacters(query)
                listUiState = if (results.isEmpty()) {
                    ListUiState.Empty
                } else {
                    ListUiState.Success(results)
                }
            } catch (e: Exception) {
                listUiState = ListUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            loadCharacters(newQuery.ifBlank { null })
        }
    }

    fun loadCharacterDetail(id: Int) {
        viewModelScope.launch {
            detailUiState = DetailUiState.Loading
            try {
                val character = repository.getCharacter(id)
                detailUiState = DetailUiState.Success(character)
            } catch (e: Exception) {
                detailUiState = DetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun toggleFavorite(character: Character) {
        favorites = if (favorites.any { it.id == character.id }) {
            favorites.filter { it.id != character.id }.toSet()
        } else {
            favorites + character
        }
    }

    fun isFavorite(characterId: Int): Boolean {
        return favorites.any { it.id == characterId }
    }
}
