package com.confraajuda.routes

import com.confraajuda.repository.CampaignRepository
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.campaignRoutes(campaignRepository: CampaignRepository) {
    route("/campaigns") {
        get {
            call.respond(campaignRepository.getAll())
        }
    }
}
