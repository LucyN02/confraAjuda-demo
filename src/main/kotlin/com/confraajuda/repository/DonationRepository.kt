package com.confraajuda.repository

import com.confraajuda.model.DonationResponse
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

class DonationRepository {
    private val donations = ConcurrentHashMap<String, DonationResponse>()
    private val simulatedCreationTimes = ConcurrentHashMap<String, LocalDateTime>()

    fun getById(id: String): DonationResponse? = donations[id]

    fun save(donation: DonationResponse, creationTime: LocalDateTime = LocalDateTime.now()) {
        donations[donation.id] = donation
        simulatedCreationTimes[donation.id] = creationTime
    }

    fun updateStatus(id: String, newStatus: String): DonationResponse? {
        val donation = donations[id] ?: return null
        val updated = donation.copy(status = newStatus)
        donations[id] = updated
        return updated
    }

    fun getSimulatedCreationTime(id: String): LocalDateTime? = simulatedCreationTimes[id]
}
