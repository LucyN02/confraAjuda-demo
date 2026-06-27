package com.confraajuda.routes

import com.confraajuda.model.DonationRequest
import com.confraajuda.service.DonationService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.donationRoutes(donationService: DonationService) {
    post("/donate") {
        try {
            val req = call.receive<DonationRequest>()
            val result = donationService.donate(req)
            result.onSuccess { response ->
                call.respond(response)
            }.onFailure { error ->
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (error.message ?: "Erro desconhecido")))
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Formato de requisição inválido", "details" to e.message))
        }
    }

    get("/status/{id}") {
        val id = call.parameters["id"] ?: ""
        val donation = donationService.checkDonationStatus(id)
        if (donation == null) {
            call.respond(HttpStatusCode.NotFound, mapOf("error" to "Doação não encontrada"))
        } else {
            call.respond(donation)
        }
    }

    post("/confirm/{id}") {
        val id = call.parameters["id"] ?: ""
        val donation = donationService.confirmDonationManually(id)
        if (donation == null) {
            call.respond(HttpStatusCode.NotFound, mapOf("error" to "Doação não encontrada"))
        } else {
            call.respond(donation)
        }
    }
}
