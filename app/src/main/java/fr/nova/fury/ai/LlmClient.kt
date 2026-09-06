package fr.nova.fury.ai

interface LlmClient {
    suspend fun complete(config: RemoteAiConfig, userText: String, novaContext: String?): AiResponse
}
