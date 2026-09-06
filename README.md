# NOVA V8 — Personal Command Center

NOVA V8 est la base mobile Android du Personal Command Center de NOVA.

## Objectif

Centraliser les projets, missions, données personnelles et futurs services intelligents dans une architecture mobile évolutive.

## Socle V8

- Android / Kotlin / Jetpack Compose
- Architecture modulaire
- Core NOVA
- Missions et projets
- Mémoire et contexte préparés
- GitHub Actions pour construire l’APK
- Utilisation mobile via GitSync → GitHub → APK

## Version

- Version : 8.0.0
- Codename : PERSONAL COMMAND CENTER
- Cible : Android / Google Pixel

## Règle de continuité

La branche principale de développement est `main`. Toute évolution doit préserver la compatibilité de build Android et ne doit pas introduire de ressources invalides dans `app/src/main/res/`.
