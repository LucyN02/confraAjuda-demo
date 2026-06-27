package com.confraajuda.model

import kotlinx.serialization.Serializable

@Serializable
data class DonationRequest(
    val campaignId: String,
    val customerName: String,
    val customerDocument: String,
    val amount: Double
)

@Serializable
data class DonationResponse(
    val id: String,
    val campaignId: String,
    val customerName: String,
    val amount: Double,
    val pixCode: String,
    val status: String,
    val expirationDate: String
)
