# CHANGELOG NOVA

## v5.2.0 — STABILISATION & DESIGN RÉELLEMENT BRANCHÉ

### Corrigé
- CI : suppression de `build_apk.yml`, un fragment de workflow invalide qui faisait doublon avec `android-build.yml` (seule référence de build conservée, conforme à MOBILE_CHECKLIST.md).
- Version incohérente entre `VERSION.json` (5.1.1) et `app/build.gradle.kts` (5.0.0) : alignées sur 5.2.0.
- L'écran d'accueil réel (`NovaFuryApp.Home`) n'utilisait ni `NovaTheme`, ni `NovaColors`, ni les composants `NovaCard`/`NovaModuleCard`/`NovaStat` déjà écrits dans `ui/components` et `ui/theme` : ils existaient mais n'étaient jamais appelés. L'app tournait avec un Material3 générique à couleurs dynamiques Android, pas l'identité visuelle NOVA.
- `launchVoice` était dupliqué (une version privée dans `HomeScreen.kt`, une publique dans `Voice.kt`) : un seul point d'entrée conservé.
- Les statistiques de l'accueil (12 tâches, 3 événements, 7 projets, 1 objectif) étaient codées en dur, jamais reliées à `NovaState` : remplacées par de vrais calculs (missions terminées/ouvertes, opportunités suivies, score de la priorité n°1).
- Suppression d'un bloc décoratif vide (`Box` gris de 120dp sans contenu) sur la carte de citation.

### Reste à faire (audité, non traité ici)
- Missions, Business Hunter, Watch Lab, Money OS, Second Brain, Focus et Settings utilisent encore le Material3 par défaut (`ElevatedCard`), pas les composants NOVA — c'est le chantier V6.2 de la roadmap.
- Aucune vérification de compilation réelle n'a été possible ici (bac à sable sans accès réseau) : à valider via GitHub Actions après le push.

## v5.1.1 — ONE CLICK MOBILE 🔥

### Nouveau
- Bootstrap Gradle inclus pour la compilation cloud.
- Workflow GitHub Actions simplifié : un push = un build.
- Vérifications automatiques de la structure du projet.
- Artifact APK nommé clairement.
- Guide spécial Pixel.
- Checklist de dépannage mobile.

### Philosophie
**Tu ne compiles pas sur ton Pixel.**
Ton Pixel pilote GitHub.
GitHub compile dans le cloud.
Ton Pixel installe le résultat.

## v5.1.0 — GitHub Mobile Edition
- Base GitHub Actions.
- Branches main/dev/fury.
- Formulaires idées et bugs.
