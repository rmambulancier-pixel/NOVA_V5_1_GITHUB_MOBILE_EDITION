# Build sans ordinateur

Le build est effectué par GitHub Actions.

Le projet doit contenir le Gradle Wrapper (`gradlew`, `gradlew.bat`, `gradle/wrapper/...`) pour la compilation cloud.

Si ton dépôt provient d'un projet ouvert une fois dans Android Studio, ces fichiers sont générés automatiquement.
Pour un flux totalement mobile, tu peux aussi initialiser le dépôt avec GitHub Codespaces puis lancer `./gradlew wrapper` dans le terminal cloud.

Une fois le Wrapper présent, GitHub Actions compile l'APK automatiquement.
