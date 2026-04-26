package com.example.myapplication.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

actual fun createHttpClient(): HttpClient =
    HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(AppJson)
        }
    }

