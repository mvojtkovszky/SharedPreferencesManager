package com.vojtkovszky.sharedpreferencesmanager.model

import kotlinx.serialization.Serializable

@Serializable
data class Dog(
    val name: String,
    val breed: String,
    val weightGrams: Int
)