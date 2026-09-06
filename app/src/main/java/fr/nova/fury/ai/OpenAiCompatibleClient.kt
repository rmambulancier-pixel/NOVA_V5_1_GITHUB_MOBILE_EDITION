package fr.nova.fury.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class OpenAiCompatibleClient : LlmClient {
    override suspend fun complete(config: RemoteAiConfig, userText: String, novaContext: String?): AiResponse = withContext(Dispatchers.IO) {
        val messages = JSONArray().apply {
            put(JSONObject().apply {
                put("role", "system")
                put("content", "Tu es Alphonse, copilote de NOVA. Réponds en français, concrètement et honnêtement. Ne prétends jamais avoir exécuté une action que tu n'as pas exécutée.")
            })
            if (!novaContext.isNullOrBlank()) put(JSONObject().apply { put("role", "system"); put("content", novaContext) })
            put(JSONObject().apply { put("role", "user"); put("content", userText) })
        }
        val body = JSONObject().apply { put("model", config.model); put("messages", messages) }.toString()
        val connection = (URL(config.endpoint.trim()).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 20_000
            readTimeout = 60_000
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Authorization", "Bearer ${config.token}")
        }
        connection.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
        val code = connection.responseCode
        val stream = if (code in 200..299) connection.inputStream else connection.errorStream
        val raw = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
        if (code !in 200..299) throw IllegalStateException("HTTP $code: ${raw.take(500)}")
        val content = JSONObject(raw).optJSONArray("choices")?.optJSONObject(0)?.optJSONObject("message")?.optString("content")
            ?.takeIf { it.isNotBlank() } ?: throw IllegalStateException("Réponse IA vide ou incompatible.")
        AiResponse(content, AiRoute.REMOTE, config.model)
    }
}
