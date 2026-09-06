# Architecture NOVA V5

MainActivity
    ↓
NovaFuryApp (Compose)
    ↓
NovaState ← NovaStore/DataStore
    ↓
NovaEngine
    ├── Mission scoring
    ├── Deal scoring
    ├── Watch concept scoring
    ├── System health
    └── Voice command routing

Principe :
UI → State → Store → DataStore
       ↓
    NovaEngine

Aucun serveur requis.
Aucune clé API requise.
Aucun compte requis.
