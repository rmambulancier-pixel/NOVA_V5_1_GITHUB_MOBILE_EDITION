package fr.nova.fury

import java.util.UUID
import fr.nova.fury.ai.AiMode

enum class Area { CAPITAL, WATCH, NOVA, PERSONAL }

data class Mission(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val area: Area = Area.PERSONAL,
    val impact: Int = 3,
    val effort: Int = 3,
    val urgency: Int = 3,
    val done: Boolean = false
)

data class Deal(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val buy: Double,
    val sell: Double,
    val fees: Double = 0.0,
    val shipping: Double = 0.0,
    val risk: Int = 30,
    val confidence: Int = 70,
    val demand: Int = 70
) {
    val profit: Double get() = sell - buy - fees - shipping
    val roi: Double get() = if (buy == 0.0) 0.0 else profit / buy * 100
}

data class WatchConcept(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val movement: String,
    val caseDesign: String,
    val dial: String,
    val targetPrice: Double,
    val estimatedCost: Double,
    val originality: Int,
    val brandFit: Int,
    val feasibility: Int,
    val caseShape: String = "Round",
    val caseSize: Int = 40,
    val dialStyle: String = "Minimal",
    val handStyle: String = "Dauphine",
    val strapStyle: String = "Leather",
    val accentStyle: String = "Steel"
    val feasibility: Int
)

data class CashFlow(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val income: Boolean,
    val category: String
)

data class NovaState(
    val missions: List<Mission> = listOf(
        Mission(title = "Trouver une opportunité rentable", area = Area.CAPITAL, impact = 5, effort = 2, urgency = 5),
        Mission(title = "Définir la signature de la marque", area = Area.WATCH, impact = 5, effort = 4, urgency = 3),
        Mission(title = "Faire de NOVA un outil quotidien", area = Area.NOVA, impact = 4, effort = 3, urgency = 4)
    ),
    val deals: List<Deal> = emptyList(),
    val watches: List<WatchConcept> = emptyList(),
    val cash: List<CashFlow> = emptyList(),
    val brain: List<String> = listOf("NOVA V8 initialisé. Personal Command Center prêt."),
    val dark: Boolean = true,
    val biometricLock: Boolean = false
    val brain: List<String> = listOf("NOVA V5 initialisé. Je suis prêt à organiser ton système."),
    val dark: Boolean = true,
    val biometricLock: Boolean = false,
    val aiMode: AiMode = AiMode.AUTO,
    val aiEndpoint: String = "",
    val aiModel: String = ""
)
