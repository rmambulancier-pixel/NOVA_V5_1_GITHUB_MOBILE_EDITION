package fr.nova.fury

import fr.nova.fury.ai.AiMode

/*
 * À intégrer dans Models.kt.
 *
 * Dans NovaState, ajouter :
 *
 *     val aiMode: AiMode = AiMode.AUTO,
 *     val aiEndpoint: String = "",
 *     val aiModel: String = "",
 *
 * Le token n'est volontairement PAS stocké dans NovaState.
 * Il doit être fourni à la session par une couche sécurisée.
 */
