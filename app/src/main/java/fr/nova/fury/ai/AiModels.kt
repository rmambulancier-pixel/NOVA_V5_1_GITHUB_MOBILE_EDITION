package fr.nova.fury.ai

enum class AiMode { LOCAL_ONLY, AUTO, REMOTE_ONLY }
enum class AiRoute { LOCAL, REMOTE, FALLBACK_LOCAL }

data class AiRequest(
    val text: String,
    val mode: AiMode = AiMode.AUTO,
    val allowRemote: Boolean = false,
    val includeNovaSummary: Boolean = true
)

data class AiResponse(
    val text: String,
    val route: AiRoute,
    val providerLabel: String? = null,
    val error: String? = null
)

data class RemoteAiConfig(
    val endpoint: String,
    val model: String,
    val token: String? = null
) {
    val isReady: Boolean get() = endpoint.isNotBlank() && model.isNotBlank() && !token.isNullOrBlank()
}
