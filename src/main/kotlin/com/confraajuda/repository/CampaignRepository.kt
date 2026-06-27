package com.confraajuda.repository

import com.confraajuda.model.Campaign
import java.util.concurrent.ConcurrentHashMap

class CampaignRepository {
    private val campaigns = ConcurrentHashMap<String, Campaign>().apply {
        put("abrigo-patinhas", Campaign(
            id = "abrigo-patinhas",
            title = "Abrigo Patinhas Felizes",
            description = "Ajude na compra de ração, medicamentos e vacinas para 150 cães e gatos resgatados das ruas pelo abrigo municipal.",
            targetAmount = 5000.0,
            currentAmount = 1850.0,
            category = "Causa Animal"
        ))
        put("biblioteca-bairro", Campaign(
            id = "biblioteca-bairro",
            title = "Reconstrução da Biblioteca Comunitária",
            description = "Após fortes chuvas, precisamos arrecadar fundos para reformar o telhado, trocar prateleiras e repor livros destruídos.",
            targetAmount = 12000.0,
            currentAmount = 4320.0,
            category = "Educação"
        ))
        put("horta-comunitaria", Campaign(
            id = "horta-comunitaria",
            title = "Horta Solidária Urbana",
            description = "Compra de mudas, sementes, ferramentas e adubo orgânico para expandir a horta do bairro, fornecendo alimentos para famílias carentes.",
            targetAmount = 2500.0,
            currentAmount = 980.0,
            category = "Sustentabilidade"
        ))
    }

    fun getAll(): List<Campaign> = campaigns.values.toList()

    fun getById(id: String): Campaign? = campaigns[id]

    fun updateAmount(id: String, additionalAmount: Double): Campaign? {
        val campaign = campaigns[id] ?: return null
        synchronized(campaign) {
            campaign.currentAmount += additionalAmount
        }
        return campaign
    }
}
