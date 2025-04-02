package com.nlkprojects.kwordle.game.words

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

interface DefinitionFetching {
    suspend fun fetchDefinition(word: String): Result<WordDto>
}

class DefinitionFetcher: DefinitionFetching {
    private val client = HttpClient(OkHttp) {
        expectSuccess = false
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }
    private val url = "https://twinword-word-graph-dictionary.p.rapidapi.com/definition/?entry="

    override suspend fun fetchDefinition(word: String): Result<WordDto> {
        return try {
            Result.success(
                client.get("$url$word") {
                    header("x-rapidapi-host", "twinword-word-graph-dictionary.p.rapidapi.com")
                    @Suppress("SpellCheckingInspection")
                    header("x-rapidapi-key", "e1d4542679msh693eb8eb41d5bbfp1c6ec7jsne4bc329ba2c5")
                }.body() as WordDto
            )
        } catch (err: Throwable) {
            Log.e(null, "$err")
            Result.failure(err)
        }
    }
}
