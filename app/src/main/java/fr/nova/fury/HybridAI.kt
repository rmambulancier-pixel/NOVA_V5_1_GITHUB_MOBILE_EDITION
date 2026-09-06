package fr.nova.fury

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

enum class AiMode { HYBRID, LOCAL_ONLY, REMOTE_ONLY }

data class LlmSettings(
    val mode: AiMode = AiMode.HYBRID,
    val endpoint: String = "",
    val model: String = "",
    val apiKey: String = "",
    val allowRemote: Boolean = false
)

data class AiReply(
    val text: String,
    val source: String,
    val usedFallback: Boolean = false
)

/** Local brain: instant, deterministic and available without network. */
object LocalBrain {
    fun answer(prompt: String, state: NovaState): String {
        val p = prompt.lowercase().trim()
        return when {
            p.contains("prochaine") || p.contains("priorit") || p.contains("faire") -> NovaEngine.nextAction(state)
            p.contains("sant") || p.contains("score") || p.contains("état") -> "Santé NOVA : ${NovaEngine.health(state)}/100. ${NovaEngine.nextAction(state)}"
            p.contains("argent") || p.contains("cash") || p.contains("finance") -> {
                val balance = state.cash.sumOf { if (it.income) it.amount else -it.amount }
                "Solde enregistré : %.2f €. %s".format(balance, NovaEngine.nextAction(state))
            }
            p.contains("montre") || p.contains("watch") -> state.watches.maxByOrNull(NovaEngine::watchScore)?.let {
                "Concept leader : ${it.name}, score ${NovaEngine.watchScore(it)}/100."
            } ?: "Aucun concept montre n'est encore enregistré dans Watch Lab."
            p.contains("business") || p.contains("opportun") || p.contains("deal") -> state.deals.maxByOrNull(NovaEngine::dealScore)?.let {
                "Meilleure opportunité : ${it.title}, score ${NovaEngine.dealScore(it)}/100, profit estimé %.2f €.".format(it.profit)
            } ?: "Aucune opportunité analysée pour le moment."
            else -> "Mode local : je peux analyser tes priorités, missions, opportunités, finances et concepts de montre. Pour une analyse ouverte et complexe, active l'IA distante dans Settings."
        }
    }
}

/** Generic OpenAI-compatible /v1/chat/completions client. No provider SDK is embedded. */
class RemoteLlmClient {
    suspend fun ask(settings: LlmSettings, prompt: String, state: NovaState): String = withContext(Dispatchers.IO) {
        require(settings.endpoint.startsWith("https://")) { "L'endpoint distant doit utiliser HTTPS." }
        require(settings.model.isNotBlank()) { "Choisis un modèle distant." }
        require(settings.apiKey.isNotBlank()) { "Ajoute une clé API avant d'activer l'IA distante." }

        val system = """Tu es Alphonse, copilote de NOVA sur Android. Réponds en français, de façon concise et actionnable. Tu reçois un cockpit local; ne prétends pas avoir accès à Internet ou à des données absentes. Contexte NOVA: ${contextSummary(state)}"""
        val messages = JSONArray().put(JSONObject().put("role", "system").put("content", system))
        // Historique récent : donne à Alphonse la continuité de la conversation, pas juste la dernière question.
        state.alphonseHistory.takeLast(12).forEach { turn ->
            messages.put(JSONObject().apply {
                put("role", if (turn.role == AlphonseRole.USER) "user" else "assistant")
                put("content", turn.text)
            })
        }
        messages.put(JSONObject().put("role", "user").put("content", prompt))
        val payload = JSONObject().apply {
            put("model", settings.model)
            put("messages", messages)
            put("temperature", 0.4)
        }
        val connection = (URL(settings.endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 15_000
            readTimeout = 30_000
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Authorization", "Bearer ${settings.apiKey}")
        }
        try {
            connection.outputStream.use { it.write(payload.toString().toByteArray(Charsets.UTF_8)) }
            val stream = if (connection.responseCode in 200..299) connection.inputStream else connection.errorStream
            val body = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            if (connection.responseCode !in 200..299) error("API distante ${connection.responseCode}: ${body.take(240)}")
            val json = JSONObject(body)
            json.optJSONArray("choices")?.optJSONObject(0)?.optJSONObject("message")?.optString("content")
                ?.takeIf { it.isNotBlank() } ?: error("Réponse LLM distante non reconnue.")
        } finally { connection.disconnect() }
    }

    private fun contextSummary(s: NovaState): String =
        "missions=${s.missions.count { !it.done }}/${s.missions.size}; deals=${s.deals.size}; watches=${s.watches.size}"
}

class HybridAiRouter(private val context: Context) {
    private val remote = RemoteLlmClient()

    suspend fun ask(prompt: String, state: NovaState): AiReply {
        val settings = NovaStore(context).loadLlmSettings()
        val wantsComplex = prompt.trim().split(Regex("\\s+")).size > 12 ||
            listOf("analyse", "stratégie", "compare", "rédige", "plan", "pourquoi").any { it in prompt.lowercase() }
        val useRemote = when (settings.mode) {
            AiMode.LOCAL_ONLY -> false
            AiMode.REMOTE_ONLY -> true
            AiMode.HYBRID -> settings.allowRemote && wantsComplex
        }
        if (!useRemote) return AiReply(LocalBrain.answer(prompt, state), "LOCAL")
        return try {
            AiReply(remote.ask(settings, prompt, state), "LLM DISTANT")
        } catch (e: Exception) {
            if (settings.mode == AiMode.REMOTE_ONLY) AiReply("IA distante indisponible : ${e.message}", "ERREUR")
            else AiReply(LocalBrain.answer(prompt, state), "LOCAL (FALLBACK)", true)
        }
    }
}
