package com.confraajuda.service

import com.confraajuda.model.*
import com.confraajuda.repository.CampaignRepository
import com.confraajuda.repository.DonationRepository
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DonationService(
    private val campaignRepository: CampaignRepository,
    private val donationRepository: DonationRepository,
    private val confraPixService: ConfraPixService
) {
    private val logger = LoggerFactory.getLogger(DonationService::class.java)

    suspend fun donate(req: DonationRequest): Result<DonationResponse> {
        val campaign = campaignRepository.getById(req.campaignId)
            ?: return Result.failure(IllegalArgumentException("Campanha não encontrada"))

        val expirationDate = LocalDateTime.now().plusHours(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        
        val txResult = confraPixService.createPixTransaction(
            amount = req.amount,
            customerName = req.customerName,
            customerDocument = req.customerDocument,
            description = "Doacao para campanha ${campaign.title}",
            expirationDate = expirationDate
        )

        return txResult.map { tx ->
            val donation = DonationResponse(
                id = tx.uuid,
                campaignId = req.campaignId,
                customerName = req.customerName,
                amount = tx.amount,
                pixCode = tx.pix_code ?: "Pix indísponível",
                status = tx.status,
                expirationDate = tx.expiration_date ?: expirationDate
            )
            donationRepository.save(donation)
            logger.info("Doação registrada: ${donation.id} | Status inicial: ${donation.status}")
            donation
        }
    }

    suspend fun checkDonationStatus(id: String): DonationResponse? {
        val donation = donationRepository.getById(id) ?: return null

        if (donation.status == "processing") {
            if (confraPixService.mockMode) {
                // Modo Simulação: auto-aprova após 10 segundos
                val creationTime = donationRepository.getSimulatedCreationTime(id)
                if (creationTime != null && LocalDateTime.now().isAfter(creationTime.plusSeconds(10))) {
                    val updated = donationRepository.updateStatus(id, "succeeded")
                    if (updated != null) {
                        campaignRepository.updateAmount(donation.campaignId, donation.amount)
                        logger.info("[MOCK] Doação $id auto-confirmada após tempo.")
                        return updated
                    }
                }
            } else {
                // Modo Real: faz consulta na API ConfraPix
                val statusResult = confraPixService.getTransactionStatus(id)
                statusResult.onSuccess { tx ->
                    if (tx.status != donation.status) {
                        val updated = donationRepository.updateStatus(id, tx.status)
                        if (tx.status == "succeeded") {
                            campaignRepository.updateAmount(donation.campaignId, donation.amount)
                        }
                        logger.info("[API] Doação $id atualizada para status: ${tx.status}")
                        if (updated != null) return updated
                    }
                }.onFailure { e ->
                    logger.error("Erro ao verificar status na API para doação $id", e)
                }
            }
        }

        return donationRepository.getById(id)
    }

    fun confirmDonationManually(id: String): DonationResponse? {
        val donation = donationRepository.getById(id) ?: return null
        if (donation.status == "processing") {
            val updated = donationRepository.updateStatus(id, "succeeded")
            campaignRepository.updateAmount(donation.campaignId, donation.amount)
            logger.info("[SIMULATION] Confirmação manual acionada para: $id")
            return updated
        }
        return donation
    }
}
