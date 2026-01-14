package com.example.apiapp.data.repository

import com.example.apiapp.data.api.RickAndMortyApi
import com.example.apiapp.data.model.Character
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RickAndMortyRepository {
    private val api: RickAndMortyApi

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://rickandmortyapi.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        api = retrofit.create(RickAndMortyApi::class.java)
    }

    suspend fun getCharacters(name: String? = null): List<Character> {
        return try {
            api.getCharacters(name).results
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getCharacter(id: Int): Character {
        return api.getCharacter(id)
    }
}
