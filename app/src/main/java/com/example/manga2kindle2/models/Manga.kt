package com.example.manga2kindle2.models

import io.github.agrevster.pocketbaseKotlin.models.Record
import kotlinx.serialization.Serializable

@Serializable
data class Manga(
    val title: String,
    val author: String?,
): Record()
