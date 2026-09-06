# Installation sur Pixel avec GitSync

1. Dans GitSync, télécharge d'abord les modifications du dépôt pour être à jour.
2. Garde le dossier `.git` existant : ne le remplace jamais avec celui du ZIP.
3. Décompresse NOVA V7.0 dans un dossier temporaire.
4. Copie le contenu du dossier du projet V7.0 dans ton dossier local GitSync NOVA, en remplaçant les fichiers demandés.
5. Ne copie pas de dossier `.git` depuis le ZIP : il n'y en a volontairement pas.
6. Dans GitSync, vérifie les fichiers modifiés, puis commit : `NOVA V7.0 Hybrid Core`.
7. Téléverse les modifications vers `origin/main`.
8. Ouvre GitHub Actions et vérifie la compilation APK.

Avant tout `Téléverser + Écraser`, préfère un téléchargement/pull normal et la vérification des différences.
