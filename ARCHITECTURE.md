# Architecture NOVA V8

## Application

```text
app/
└── src/main/
    ├── java/fr/nova/fury/
    │   ├── Core et modèles
    │   ├── Engine et IA hybride
    │   ├── Stockage
    │   ├── Voice
    │   └── UI Compose
    └── res/
        ├── drawable/
        ├── mipmap*/
        └── values/
```

## Direction V8

La V8 sert de fondation pour :

1. Conversations persistantes
2. Mémoire structurée
3. Gestion des projets et fichiers
4. Moteur de décisions et de tâches
5. Services intelligents modulaires
6. Notifications et automatisations futures

Chaque nouveau module doit rester découplé afin de permettre l’évolution vers les versions suivantes sans reconstruire l’application.
