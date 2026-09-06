package fr.nova.fury.ai

import fr.nova.fury.NovaState

class AlphonseSession(private val router: AlphonseRouter = AlphonseRouter()) {
    private var remoteConfig: RemoteAiConfig? = null
    fun configureRemote(endpoint: String, model: String, token: String) { remoteConfig = RemoteAiConfig(endpoint, model, token) }
    fun clearRemoteToken() { remoteConfig = remoteConfig?.copy(token = null) }
    suspend fun ask(text: String, state: NovaState, mode: AiMode, allowRemote: Boolean): AiResponse =
        router.ask(AiRequest(text, mode, allowRemote), state, remoteConfig)
}
