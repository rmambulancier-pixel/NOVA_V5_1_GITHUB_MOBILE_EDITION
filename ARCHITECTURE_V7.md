# Architecture NOVA V7

## Flux

Utilisateur
  ↓
AlphonseRouter
  ├── LOCAL
  │     ├── NovaEngine
  │     ├── NovaState
  │     └── DataStore
  │
  ├── AUTO
  │     ├── commande déterministe → LOCAL
  │     ├── question liée aux données NOVA → LOCAL
  │     └── analyse ouverte → REMOTE si configuré
  │
  └── REMOTE
        ↓
    LlmClient
        ↓
    API compatible Chat Completions

## Règle fondamentale
Le LLM distant n'est jamais la source de vérité de NOVA.

La source de vérité reste :
`NovaState + NovaStore + NovaEngine`.

Le LLM reçoit un contexte réduit et explicite. Il ne reçoit jamais
automatiquement tout le contenu de l'utilisateur.

## Modes
- LOCAL_ONLY : aucune sortie réseau.
- AUTO : local si possible, distant si nécessaire et autorisé.
- REMOTE_ONLY : l'utilisateur force l'analyse distante.

## Échec réseau
REMOTE_ONLY → réponse d'erreur claire.
AUTO → repli local.
LOCAL_ONLY → aucun appel réseau possible.
