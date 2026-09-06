# Correctif V7.0 — ressources Android

Correction du build `:app:mergeDebugResources` :

- les PNG de prévisualisation ont été déplacés de `app/src/main/res/previews/` vers `/previews/` ;
- `LOGO_README.md` a été déplacé hors de `app/src/main/res/` vers `/docs/`.

Android ne reconnaît pas `previews` comme un type de ressource valide. Dans `app/src/main/res/`, les fichiers doivent être placés dans des dossiers de ressources Android valides (par exemple `drawable`, `mipmap`, `values`, etc.).
