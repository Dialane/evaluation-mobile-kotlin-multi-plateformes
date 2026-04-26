# Éléments de validation — Grille d’évaluation Mobile (Kotlin Multiplatform)

Ce document décrit, **critère par critère**, ce qui est effectivement mis en place dans l’application afin de démontrer les compétences attendues par la grille fournie.

## Périmètre de l’application

- Projet **Kotlin Multiplatform + Compose Multiplatform** avec cibles :
  - `androidMain` (Android)
  - `jvmMain` (Desktop JVM)
  - `commonMain` (code partagé)
- Projet **mono-module** (`:composeApp`) mais structuré en **couches** via des packages (Clean Architecture).
- Fonctionnel : listing de `Location` (page 1) + détail, avec une navigation Android et un layout master-detail Desktop.

Fichiers clés :
- Build KMP : `composeApp/build.gradle.kts`
- Description architecture : `README.md`

---

## CRIT-DMA-D3-S-1 — Connaissances théoriques Clean Architecture avec Kotlin Multiplatform

### Ce qui est mis en place
- **Spécificités KMP & structure** : séparation claire via `commonMain` / `androidMain` / `jvmMain`.
- **Architecture en couches** (Clean Architecture) matérialisée par les packages :
  - `domain` : modèles métier + contrats
  - `data` : implémentations techniques (remote/local + mapping)
  - `presentation` : UI Compose + UDF/MVI (state/intents/effects)
- **Découplage** :
  - Le `Domain` ne dépend d’aucune techno (pas de Ktor/Settings/Compose).
  - La `Presentation` dépend d’abstractions du `Domain` (usecases/repository), pas d’implémentations `data`.
- **Expect/Actual** utilisé pour gérer des implémentations spécifiques plateformes (audio, back, engine HTTP).

### Preuves (fichiers)
- `composeApp/src/commonMain/kotlin/com/example/myapplication/domain/location/LocationRepository.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/data/location/DefaultLocationRepository.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/presentation/ui/AppRoot.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/cross/SoundManager.kt`
- `composeApp/src/androidMain/kotlin/com/example/myapplication/cross/SoundManager.android.kt`
- `composeApp/src/jvmMain/kotlin/com/example/myapplication/cross/SoundManager.jvm.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/data/network/HttpClientFactory.kt`
- `composeApp/src/androidMain/kotlin/com/example/myapplication/data/network/HttpClientFactory.android.kt`
- `composeApp/src/jvmMain/kotlin/com/example/myapplication/data/network/HttpClientFactory.jvm.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/presentation/platform/PlatformBackHandler.kt`
- `composeApp/src/androidMain/kotlin/com/example/myapplication/presentation/platform/PlatformBackHandler.android.kt`
- `composeApp/src/jvmMain/kotlin/com/example/myapplication/presentation/platform/PlatformBackHandler.jvm.kt`

---

## CRIT-DMA-D3-SE-1 — Structuration et qualité du code dans un projet KMP

### Ce qui est mis en place
- **Structure modulaire KMP** : `composeApp/src/commonMain`, `composeApp/src/androidMain`, `composeApp/src/jvmMain`.
- **Gestion des dépendances** lisible dans `composeApp/build.gradle.kts` (dépendances par sourceSet).
- **Documentation** :
  - `README.md` explique la structure et la répartition `domain/data/presentation`, ainsi que les usages `expect/actual`.
- **Conventions de nommage & organisation** :
  - packages cohérents (`domain`, `data`, `presentation`, puis par feature : `locationlist`, `locationdetail`, etc.).
  - écrans et composants nommés explicitement (`LocationListScreen`, `LocationDetailScreen`, `Tag`, etc.).

### Preuves (fichiers)
- `composeApp/build.gradle.kts`
- `README.md`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/presentation/locationlist/LocationListScreen.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/presentation/locationdetail/LocationDetailScreen.kt`

---

## CRIT-DMA-D3-SF-1 — Intégration en Clean Architecture + gestion multi-plateforme

### Ce qui est mis en place
- **Séparation Presentation / Domain / Data** :
  - `Presentation` utilise des *usecases* (`GetLocations`, `GetLocationDetail`) construits à partir d’un `LocationRepository` (contrat du Domain).
  - `Data` fournit l’implémentation `DefaultLocationRepository` (remote + cache), sans remonter de DTOs vers la `Presentation`.
- **Expect/Actual** appliqué à des besoins typiquement plateformes :
  - engine HTTP Ktor (OkHttp Android / CIO Desktop),
  - gestion du bouton back (Android seulement),
  - audio UI click (Android SoundPool / Desktop MP3 ou beep).
- **Injection / wiring** :
  - injection faite via un petit contrat `AppDependencies` + implémentation `DefaultAppDependencies`,
  - composition root par plateforme (`Context.createAppDependencies()` côté Android, `createAppDependencies()` côté Desktop).

### Points à noter vs grille
- La grille mentionne **Koin** : ici l’injection est **manuelle** (composition root), ce qui garde le découplage mais ne démontre pas l’intégration Koin.

### Preuves (fichiers)
- `composeApp/src/commonMain/kotlin/com/example/myapplication/di/AppDependencies.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/di/DefaultAppDependencies.kt`
- `composeApp/src/androidMain/kotlin/com/example/myapplication/di/AndroidAppDependencies.kt`
- `composeApp/src/jvmMain/kotlin/com/example/myapplication/di/JvmAppDependencies.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/domain/location/usecase/GetLocations.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/domain/location/usecase/GetLocationDetail.kt`

---

## CRIT-DMA-D2-S-2 — Connaissances théoriques sur l’architecture de la couche Data

### Ce qui est mis en place
- **Couple Service/DTO (remote)** :
  - `LocationApi` (service HTTP),
  - `LocationDto`, `LocationsResponseDto` (DTO sérialisables).
- **Couple Local “DAO-like” / Entity (local)** :
  - `LocationCache` encapsule la lecture/écriture (rôle “DAO-like”),
  - `CachedLocationSummary` / `CachedLocationDetail` jouent le rôle d’entités stockées (JSON dans Settings).
- **Mécanisme de fetch (2 sources)** :
  - lecture du cache si disponible (sauf `forceRefresh`),
  - sinon remote, écriture en cache,
  - fallback cache si le remote échoue et que des données existent.
- **Mapping et transit entre couches** :
  - mapping `DTO -> Domain` (LocationDto -> LocationSummary/LocationDetail),
  - mapping `Cache Entity -> Domain` et `Domain -> Cache Entity`,
  - le `Domain` reste indépendant : il ne voit ni DTO ni entités de cache.

### Preuves (fichiers)
- `composeApp/src/commonMain/kotlin/com/example/myapplication/data/location/remote/LocationApi.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/data/location/remote/dto/LocationDto.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/data/location/local/LocationCache.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/data/location/DefaultLocationRepository.kt`

---

## CRIT-DMA-D2-SE-1 — Améliorer l’expérience de développement de la couche Data

### Ce qui est mis en place
- Code **découpé** par responsabilité :
  - `remote` (API + DTO),
  - `local` (cache + entités),
  - `DefaultLocationRepository` (orchestration fetch + mapping).
- **Réutilisabilité** et limitation de duplication :
  - fonctions de mapping dédiées (`toSummary()`, `toDetail()`, `toCache()`, `toDomain()`),
  - helper `getStringOrNullCompat()` centralise un comportement spécifique à `Settings`.
- **Nommage explicite** (`LocationCache`, `DefaultLocationRepository`, `forceRefresh`, `residentIdFromUrl`, etc.).
- Existence de **KDoc ciblé** sur des points de wiring / cross-platform.

### Preuves (fichiers)
- `composeApp/src/commonMain/kotlin/com/example/myapplication/data/location/local/LocationCache.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/data/location/DefaultLocationRepository.kt`
- `composeApp/src/androidMain/kotlin/com/example/myapplication/di/AndroidAppDependencies.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/cross/SoundManager.kt`

---

## CRIT-DMA-D2-SF-4 — Intégration et adaptation à une architecture Data proposée

### Remarque
La **description détaillée** de ce critère n’apparaît pas dans la grille fournie dans le prompt (seul le titre/poids est visible).

### Ce qui est mis en place (éléments Data pertinents)
- Architecture Data complète autour de la feature `location` :
  - remote (Ktor) + local (Settings) + repository (fetch + mapping).
- Gestion des dépendances techniques via `createHttpClient()` (expect/actual) pour adapter l’engine au runtime cible.

### Preuves (fichiers)
- `composeApp/src/commonMain/kotlin/com/example/myapplication/data/location/DefaultLocationRepository.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/data/network/HttpClientFactory.kt`

---

## CRIT-DMA-D2-S-3 — Connaissances théoriques sur l’architecture de la couche Domain

### Ce qui est mis en place
- **Domain models** purs et simples (pas de dépendance à des frameworks).
- **Repository contract** défini dans le Domain :
  - la couche `data` implémente ce contrat,
  - la couche `presentation` consomme uniquement ce contrat via des usecases.
- **Usecases** (`GetLocations`, `GetLocationDetail`) encapsulent l’intention métier et fournissent un point d’entrée stable.

### Preuves (fichiers)
- `composeApp/src/commonMain/kotlin/com/example/myapplication/domain/location/LocationModels.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/domain/location/LocationRepository.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/domain/location/usecase/GetLocations.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/domain/location/usecase/GetLocationDetail.kt`

---

## CRIT-DMA-D2-SE-3 — Améliorer l’expérience de développement de la couche Domain

### Ce qui est mis en place
- **Organisation claire** par feature (`domain/location`, `domain/location/usecase`).
- **Indifférenciation / découplage** :
  - aucune dépendance à `data`/`presentation`,
  - pas de types DTO, ni d’objets `HttpClient`, ni de types UI.
- **Nommage orienté intention** : `LocationId`, `LocationSummary`, `LocationDetail`, `LocationRepository`, `GetLocationDetail`, etc.

### Preuves (fichiers)
- `composeApp/src/commonMain/kotlin/com/example/myapplication/domain/location/LocationModels.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/domain/location/usecase/GetLocations.kt`

---

## CRIT-DMA-D2-SF-3 — Intégration et adaptation à une architecture Domain proposée

### Ce qui est mis en place
- Intégration Domain-Centric complète :
  - le `Domain` définit le contrat (`LocationRepository`),
  - `Data` l’implémente (`DefaultLocationRepository`) en restant remplaçable,
  - `Presentation` consomme des usecases basés sur le contrat, sans dépendre du détail des sources.

### Preuves (fichiers)
- `composeApp/src/commonMain/kotlin/com/example/myapplication/domain/location/LocationRepository.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/data/location/DefaultLocationRepository.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/presentation/ui/AppRoot.kt`

---

## CRIT-DMA-D3-S-2 — Connaissances théoriques UI (MVI/UDF) + Jetpack Compose

### Ce qui est mis en place
- **UDF/MVI (state → render, intent → reduce)** :
  - `State` : `LocationListState`, `LocationDetailState`
  - `Intent/Action` : `LocationListIntent`, `LocationDetailIntent`
  - `Effect` (side-effect UI/navigation) : `LocationListEffect`
  - “ViewModel-like” : `LocationListStore`, `LocationDetailStore` (expose `StateFlow` + reçoit des intents)
- **Compose** :
  - écrans `LocationListScreen` / `LocationDetailScreen` ne font que du rendu et dispatchent des intents.
- **Navigation** :
  - Android : navigation `liste -> détail` gérée dans un point unique (`AppRoot` + `MobileRoute`),
  - Desktop : master-detail (liste à gauche, détail à droite) dans `AppRoot`.

### Preuves (fichiers)
- `composeApp/src/commonMain/kotlin/com/example/myapplication/presentation/locationlist/LocationListContract.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/presentation/locationlist/LocationListStore.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/presentation/locationlist/LocationListScreen.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/presentation/locationdetail/LocationDetailContract.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/presentation/locationdetail/LocationDetailStore.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/presentation/locationdetail/LocationDetailScreen.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/presentation/ui/AppRoot.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/presentation/ui/MobileRoute.kt`

---

## CRIT-DMA-D3-SE-2 — Qualité de code et DX (UDF + Compose)

### Ce qui est mis en place
- **Séparation nette** :
  - Stores = logique (chargement, retry, mapping erreurs),
  - Screens = rendu Compose + dispatch d’intents,
  - navigation centralisée dans `AppRoot`.
- **Composants réutilisables (philosophie LEGO)** :
  - `Tag` est un composant UI partagé pour le rendu de “chips”.
- **Design System identifiable** :
  - `AppTheme` (colors + shapes) fournit une base cohérente et cross-platform.
- **Nommage explicite** et structure par feature (`locationlist`, `locationdetail`).

### Preuves (fichiers)
- `composeApp/src/commonMain/kotlin/com/example/myapplication/presentation/ui/AppRoot.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/presentation/theme/AppTheme.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/presentation/ui/components/Tag.kt`

---

## CRIT-DMA-D3-SF-2 — Application & intégration UI (MVI + Compose + navigation + design system)

### Ce qui est mis en place
- **Single Activity** :
  - toute l’app Android vit dans `MainActivity` via `setContent { App(...) }`.
- **Navigation centralisée** :
  - Android : route unique via `MobileRoute` dans `AppRoot`,
  - back géré via `PlatformBackHandler` (expect/actual).
- **Design System** :
  - `AppTheme` + composants UI (`Tag`) réutilisés dans les écrans.
- **Écrans vs logique** :
  - `LocationListStore` / `LocationDetailStore` gèrent état et événements,
  - `LocationListScreen` / `LocationDetailScreen` affichent et déclenchent des intents.

### Preuves (fichiers)
- `composeApp/src/androidMain/kotlin/com/example/myapplication/MainActivity.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/presentation/ui/AppRoot.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/presentation/platform/PlatformBackHandler.kt`
- `composeApp/src/commonMain/kotlin/com/example/myapplication/presentation/theme/AppTheme.kt`

