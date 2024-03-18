package com.muedsa.uitl

import kotlinx.serialization.json.Json

val LenientJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
}