package com.confraajuda.model

import kotlinx.serialization.Serializable

@Serializable
data class Campaign(
    val id: String,
    val title: String,
    val description: String,
    val targetAmount: Double,
    var currentAmount: Double,
    val category: String
)
