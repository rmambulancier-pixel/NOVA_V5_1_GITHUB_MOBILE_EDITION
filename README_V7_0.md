# NOVA V7.0 — HYBRID CORE

Version intégrée au projet NOVA réel.

## Important pour GitSync
Ce ZIP est le projet COMPLET, pas un patch.
Ne l'importez pas par-dessus un dossier contenant déjà `.git`.
Décompressez-le, puis copiez uniquement les fichiers du projet dans votre clone GitSync existant, ou utilisez-le comme nouveau clone puis reconnectez `origin`.

## Architecture
LOCAL_ONLY : aucun réseau.
AUTO : local d'abord, distant uniquement avec autorisation explicite.
REMOTE_ONLY : distant explicitement autorisé.

Aucune clé API n'est incluse ni enregistrée dans Git.
