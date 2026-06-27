package com.confraajuda.model

import kotlinx.serialization.Serializable

@Serializable
data class ConfraPixStoreRequest(
    val amount: Double,
    val customer_name: String,
    val customer_document: String,
    val description: String,
    val expiration_date: String,
    val callback_url: String
)

@Serializable
data class ConfraPixTransaction(
    val id: Int? = null,
    val uuid: String,
    val status: String,
    val type: String? = null,
    val amount: Double,
    val description: String? = null,
    val customer_name: String? = null,
    val customer_document: String? = null,
    val expiration_date: String? = null,
    val callback_url: String? = null,
    val confirmed: Boolean,
    val pix_code: String? = null
)

@Serializable
data class ConfraPixStoreResponse(
    val status: Int,
    val success: Boolean,
    val message: String,
    val transaction: ConfraPixTransaction
)
