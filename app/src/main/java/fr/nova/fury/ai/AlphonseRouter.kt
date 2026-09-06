package fr.nova.fury.ai

import fr.nova.fury.NovaEngine
import fr.nova.fury.NovaState

class AlphonseRouter(private val remote: LlmClient = OpenAiCompatibleClient()) {
    suspend fun ask(request: AiRequest, state: NovaState, config: RemoteAiConfig?): AiResponse {
        val local = canAnswerLocally(request.text)
        return when (request.mode) {
            AiMode.LOCAL_ONLY -> localAnswer(request.text, state)
            AiMode.AUTO -> if (local || !request.allowRemote || config?.isReady != true) localAnswer(request.text, state)
                else remoteOrFallback(request, state, config)
            AiMode.REMOTE_ONLY -> when {
                !request.allowRemote -> AiResponse("L'accès distant doit être explicitement autorisé.", AiRoute.LOCAL, error = "REMOTE_NOT_AUTHORIZED")
                config?.isReady != true -> AiResponse("L'IA distante n'est pas configurée.", AiRoute.LOCAL, error = "REMOTE_NOT_CONFIGURED")
                else -> remoteOrFallback(request, state, config, false)
            }
        }
    }

    private suspend fun remoteOrFallback(request: AiRequest, state: NovaState, config: RemoteAiConfig, fallback: Boolean = true): AiResponse =
        runCatching { remote.complete(config, request.text, if (request.includeNovaSummary) buildContext(state) else null) }
            .getOrElse { e -> if (fallback) localAnswer(request.text, state).copy(route = AiRoute.FALLBACK_LOCAL, error = e.message ?: "REMOTE_FAILED")
                else AiResponse("Impossible de joindre l'IA distante : ${e.message ?: "erreur inconnue"}", AiRoute.REMOTE, error = e.message ?: "REMOTE_FAILED") }

    private fun canAnswerLocally(text: String): Boolean {
        val t = text.lowercase()
        return listOf("prochaine action", "que dois-je faire", "priorité", "santé", "score nova", "état du système", "opportunité", "business", "montre", "watch").any(t::contains)
    }

    private fun localAnswer(text: String, state: NovaState): AiResponse {
        val t = text.lowercase()
        val answer = when {
            "prochaine action" in t || "que dois-je faire" in t || "priorité" in t -> NovaEngine.nextAction(state)
            "santé" in t || "score nova" in t || "état du système" in t -> "Santé NOVA : ${NovaEngine.health(state)}/100. ${NovaEngine.nextAction(state)}"
            "opportunité" in t || "business" in t -> state.deals.maxByOrNull { NovaEngine.dealScore(it) }?.let { "Meilleure opportunité : ${it.title}, score ${NovaEngine.dealScore(it)}/100, profit estimé ${"%.2f".format(it.profit)} €." } ?: "Aucune opportunité enregistrée."
            "montre" in t || "watch" in t -> state.watches.maxByOrNull { NovaEngine.watchScore(it) }?.let { "Concept leader : ${it.name}, score ${NovaEngine.watchScore(it)}/100." } ?: "Aucun concept enregistré dans Watch Lab."
            else -> NovaEngine.nextAction(state)
        }
        return AiResponse(answer, AiRoute.LOCAL, "NOVA Local Engine")
    }

    private fun buildContext(state: NovaState): String {
        val missions = state.missions.filterNot { it.done }.sortedByDescending { NovaEngine.missionScore(it) }.take(5).joinToString("\n") { "- ${it.title} (${NovaEngine.missionScore(it)}/100)" }
        val deals = state.deals.sortedByDescending { NovaEngine.dealScore(it) }.take(3).joinToString("\n") { "- ${it.title} (${NovaEngine.dealScore(it)}/100)" }
        val watches = state.watches.sortedByDescending { NovaEngine.watchScore(it) }.take(3).joinToString("\n") { "- ${it.name} (${NovaEngine.watchScore(it)}/100)" }
        return "Résumé NOVA explicitement autorisé pour cette requête.\nSanté : ${NovaEngine.health(state)}/100\n\nMissions :\n$missions\n\nOpportunités :\n$deals\n\nConcepts :\n$watches"
    }
}
