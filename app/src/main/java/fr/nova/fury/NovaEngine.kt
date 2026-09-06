package fr.nova.fury

import kotlin.math.roundToInt

object NovaEngine {

    fun missionScore(m: Mission): Int {
        val score = m.impact * 17 + m.urgency * 8 - m.effort * 5 + if (m.done) -100 else 0
        return score.coerceIn(0, 100)
    }

    fun dealScore(d: Deal): Int {
        val margin = if (d.profit > 0) 30 else 0
        val roi = (d.roi.coerceIn(0.0, 150.0) / 150.0 * 20).roundToInt()
        val confidence = (d.confidence.coerceIn(0,100) * 0.25).roundToInt()
        val demand = (d.demand.coerceIn(0,100) * 0.25).roundToInt()
        val risk = (d.risk.coerceIn(0,100) * 0.25).roundToInt()
        return (margin + roi + confidence + demand - risk).coerceIn(0,100)
    }

    fun watchScore(w: WatchConcept): Int {
        val margin = if (w.targetPrice > w.estimatedCost) 15 else -10
        return ((w.originality + w.brandFit + w.feasibility) / 3 + margin).coerceIn(0,100)
    }

    fun health(s: NovaState): Int {
        val done = s.missions.count { it.done }.toDouble() / s.missions.size.coerceAtLeast(1)
        val deal = s.deals.map(::dealScore).average().takeUnless { it.isNaN() } ?: 50.0
        val watch = s.watches.map(::watchScore).average().takeUnless { it.isNaN() } ?: 50.0
        val balance = s.cash.sumOf { if (it.income) it.amount else -it.amount }
        val money = when {
            s.cash.isEmpty() -> 50.0
            balance >= 0 -> 75.0
            else -> 25.0
        }
        return (done*35 + deal*0.25 + watch*0.25 + money*0.15).roundToInt().coerceIn(0,100)
    }

    fun nextAction(s: NovaState): String {
        val mission = s.missions.filterNot { it.done }.maxByOrNull(::missionScore)
        val deal = s.deals.maxByOrNull(::dealScore)
        val watch = s.watches.maxByOrNull(::watchScore)

        return when {
            mission != null && missionScore(mission) >= 70 ->
                "Priorité : ${mission.title}. Score ${missionScore(mission)}/100."
            deal != null && dealScore(deal) >= 75 ->
                "Opportunité forte : ${deal.title}. Score ${dealScore(deal)}/100. Vérifie le marché avant achat."
            watch != null && watchScore(watch) >= 75 ->
                "Concept leader : ${watch.name}. Score ${watchScore(watch)}/100. Passe au design détaillé."
            else ->
                "Ajoute quelques données. Plus NOVA connaît tes projets, plus Alphonse devient utile."
        }
    }

    // Commandes simples : pensées pour la dictée vocale.
    fun parseCommand(text: String): String {
        val t = text.lowercase()
        return when {
            "prochaine action" in t || "que dois-je faire" in t ->
                "ASK_NEXT"
            "nouvelle idée" in t || "mémorise" in t ->
                "MEMORY"
            "focus" in t ->
                "FOCUS"
            "business" in t || "opportunité" in t ->
                "DEAL"
            "montre" in t || "watch" in t ->
                "WATCH"
            else -> "MEMORY"
        }
    }
}
