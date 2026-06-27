package com.confraajuda

import com.confraajuda.plugins.configureHTTP
import com.confraajuda.plugins.configureRouting
import com.confraajuda.plugins.configureSerialization
import com.confraajuda.repository.CampaignRepository
import com.confraajuda.repository.DonationRepository
import com.confraajuda.service.ConfraPixService
import com.confraajuda.service.DonationService
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    embeddedServer(Netty, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val logger = LoggerFactory.getLogger("ConfraAjuda")

    // 1. Carregar Configurações do application.conf ou Variáveis de Ambiente
    val token = environment.config.propertyOrNull("confrapix.token")?.getString() ?: ""
    val apiUrl = environment.config.propertyOrNull("confrapix.apiUrl")?.getString() ?: "https://api.confrapix.com.br/api"
    val mockMode = environment.config.propertyOrNull("confrapix.mockMode")?.getString()?.toBoolean() 
        ?: (token.isEmpty() || token.startsWith("mock"))

    logger.info("Inicializando ConfraAjuda (Refatorado).")
    logger.info("Configuração - Mock Mode: $mockMode | API URL: $apiUrl | Token Configurado: ${token.isNotEmpty()}")

    // 2. Inicializar Cliente HTTP para Integração com a API do ConfraPix
    val httpClient = HttpClient(CIO) {
        install(ClientContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    // 3. Inicializar Repositórios (Persistência em Memória)
    val campaignRepository = CampaignRepository()
    val donationRepository = DonationRepository()

    // 4. Inicializar Serviços (Lógica de Negócio e Integração com API)
    val confraPixService = ConfraPixService(httpClient, token, apiUrl, mockMode)
    val donationService = DonationService(campaignRepository, donationRepository, confraPixService)

    // 5. Configurar Plugins do Servidor Ktor
    configureHTTP()
    configureSerialization()
    configureRouting(campaignRepository, donationService)
}
