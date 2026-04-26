## KMP Rick & Morty — Locations (Android + Desktop)

Application Kotlin Multiplatform (Compose Multiplatform) consommant les endpoints `Location` de la Rick and Morty API.

### Fonctionnel

- Listing des locations (chargement page 1)
- Clic sur une location
- Détail d’une location (name, type, dimension + info dérivée des `residents`)
- Android : navigation `liste -> détail` (2 écrans)
- Desktop : affichage master-detail (liste à gauche, détail à droite)

### Architecture (Clean Architecture)

Le projet reste dans un module unique (`composeApp`) mais est structuré en **couches** via le découpage de packages :

- `composeApp/src/commonMain/kotlin/com/example/myapplication/domain` : modèles métier + contrats
  - `domain/location/LocationRepository.kt` : contrat repository
  - `domain/location/LocationModels.kt` : modèles `LocationSummary` / `LocationDetail`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/data` : implémentations techniques
  - Remote : `data/location/remote/LocationApi.kt` (Ktor)
  - Local : `data/location/local/LocationCache.kt` (Multiplatform Settings)
  - Fetch : `data/location/DefaultLocationRepository.kt` (arbitrage cache/remote + fallback)
- `composeApp/src/commonMain/kotlin/com/example/myapplication/presentation` : UI Compose + UDF/MVI
  - Stores MVI : `presentation/locationlist/LocationListStore.kt`, `presentation/locationdetail/LocationDetailStore.kt`
  - Écrans : `presentation/locationlist/LocationListScreen.kt`, `presentation/locationdetail/LocationDetailScreen.kt`
  - Root : `presentation/ui/AppRoot.kt` (navigation Android + master-detail Desktop)

### Data (2 sources + fetch)

- **Source distante** : Rick and Morty API via Ktor (`LocationApi`)
- **Source locale** : cache JSON via Multiplatform Settings (`LocationCache`)
- **Stratégie** : lecture cache si possible, sinon remote. Si remote échoue, fallback cache si présent (sinon erreur).

### Cross-platform (expect/actual)

- `SoundManager` (audio) : `composeApp/src/commonMain/kotlin/com/example/myapplication/cross/SoundManager.kt`
  - Android : `ToneGenerator` (`composeApp/src/androidMain/kotlin/com/example/myapplication/cross/SoundManager.android.kt`)
  - Desktop : `Toolkit.beep()` (`composeApp/src/jvmMain/kotlin/com/example/myapplication/cross/SoundManager.jvm.kt`)
- `HttpClientFactory` (Ktor engine par plateforme) :
  - Android : OkHttp (`composeApp/src/androidMain/kotlin/com/example/myapplication/data/network/HttpClientFactory.android.kt`)
  - Desktop : CIO (`composeApp/src/jvmMain/kotlin/com/example/myapplication/data/network/HttpClientFactory.jvm.kt`)

### Android : extension de `Context`

L’injection est initialisée côté Android via une **extension de `Context`** :

- `composeApp/src/androidMain/kotlin/com/example/myapplication/di/AndroidAppDependencies.kt` (`Context.createAppDependencies()`)

Le `Context` est utilisé uniquement pour le wiring (création du Settings Android) et ne remonte pas dans le `Domain`.

## Lancer le projet

### Android

Windows :
```shell
.\gradlew.bat :composeApp:assembleDebug
```

### Desktop (JVM)

Windows :
```shell
.\gradlew.bat :composeApp:run
```

