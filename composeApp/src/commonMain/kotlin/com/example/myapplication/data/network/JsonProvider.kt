package com.example.myapplication.data.network

import kotlinx.serialization.json.Json

internal val AppJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

