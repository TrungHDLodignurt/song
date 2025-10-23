package org.company.app.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class MusicApiService{
    private val Client = HttpClient {
        install(ContentNegotiation){
            json(Json {
                prettyPrint = true
                isLenient= true
                ignoreUnknownKeys=true
            })
        }
    }
    private val API_URL = "https://static.apero.vn/techtrek/Remote_audio.json"
    suspend fun getSongs(): List<Songdto> {
        return try {
            Client.get(API_URL).body()
        } catch (e: Exception) {
            println("Error fetching songs: ${e.message}")
            emptyList()
        }
    }
}