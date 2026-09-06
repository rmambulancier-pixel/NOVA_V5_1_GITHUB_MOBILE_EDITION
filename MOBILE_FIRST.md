# 📱 NOVA — MODE D'EMPLOI 100 % PIXEL

## Ton nouveau workflow

### 1. Crée un dépôt GitHub privé
Nom conseillé : `NOVA`

### 2. Envoie les fichiers de ce projet
Depuis l'application ou le site GitHub mobile.

### 3. GitHub compile automatiquement
À chaque changement envoyé sur `main`, `dev` ou `fury`.

### 4. Télécharge l'APK
GitHub > Actions > dernier workflow > Artifacts.

### 5. Installe sur ton Pixel

---

# 🟢 MODE ULTRA SIMPLE

Tu n'es pas obligé de comprendre le code.

Ton fonctionnement avec moi :

> "Je veux que NOVA fasse X."

Je prépare :
- les fichiers à remplacer
- les nouveaux fichiers
- les modifications

Tu les mets dans GitHub depuis ton Pixel.

GitHub compile.

Tu télécharges.

Tu testes.

---

# Branches recommandées

## main
Version stable.

## dev
Version en cours de développement.

## fury 😈
Laboratoire.
On peut y tester les idées complètement folles sans casser la version stable.

---

# Bouton Release

Dans GitHub :

Actions
→ NOVA • Release APK
→ Run workflow

Tu choisis :

Tag : v5.1.1
Titre : NOVA 5.1.1
Notes : ce qui a changé

GitHub fabrique l'APK et crée une Release téléchargeable.

---

# IMPORTANT

Le projet actuel utilise volontairement un build debug pour être extrêmement simple.

Avant une publication publique sur Google Play, il faudra ajouter :
- signature release
- keystore sécurisé
- configuration Play
- versioning officiel
