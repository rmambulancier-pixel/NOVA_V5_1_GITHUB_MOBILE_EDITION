# PATCH NOVA V7.0

## 1. Ajouter les fichiers
Copier le dossier :

`app/src/main/java/fr/nova/fury/ai/`

dans le projet.

## 2. Modifier Models.kt
Ajouter les imports et modèles V7 fournis dans `Models_V7_ADD.kt`.

Puis ajouter à `NovaState` :

```kotlin
val aiMode: AiMode = AiMode.AUTO,
val aiEndpoint: String = "",
val aiModel: String = "",
```

## 3. Modifier NovaEngine.kt
Le moteur local reste prioritaire. Ajouter la méthode `canAnswerLocally()` et
utiliser `AlphonseRouter` pour les demandes de conversation.

## 4. Ne jamais mettre une clé API dans :
- GitHub ;
- `build.gradle.kts` ;
- `BuildConfig` ;
- `VERSION.json` ;
- un commit.

Le token doit être fourni à l'exécution. Pour une version finale, NOVA devra
utiliser soit Android Keystore + chiffrement, soit — de préférence — un proxy
personnel qui conserve le secret côté serveur.

## 5. Permission Internet
Ajouter dans AndroidManifest.xml :

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## 6. V7.1
Brancher `AlphonseRouter.ask()` à l'écran conversationnel.
