package com.stephennnamani.burgerrestaurantapp.core.data.models

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class Cart (
    val productId: String,
    val quantity: Int,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val updatedAt: Long = Clock.System.now().toEpochMilliseconds()
)

@OptIn(ExperimentalTime::class)
data class Favourite (
    val productId: String,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds()
)