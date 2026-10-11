# YT Search (Android)

Reconstrução original inspirada no comportamento observável da busca do YouTube.
Sem APK do YouTube no workspace, então não há evidências REA citáveis aqui;
para análise REA real, forneça o APK: `inspect_android_package` + `search_android_classes`.
Nenhum código do YouTube foi copiado.

## Estrutura
- `SearchScreen` (`ui/SearchScreen.kt`): `SearchBar`, sugestões, recentes, resultados.
- `SearchViewModel`: `StateFlow`, debounce 300ms, histórico Room, `submit()`.
- `SearchRepository`: `suggest()` e `search()` com delay; troque por `VideoSearchApi` (Retrofit).
- `data/local`: Room `search_history` (20 recentes).
- `data/remote/VideoSearchApi`: contrato `GET /search?q=` e `GET /suggest?q=`.
- `ui/VideoCard`: thumbnail placeholder, título, canal, views, duração.

## Compilar local
`gradle :app:assembleDebug` → `app/build/outputs/apk/debug/app-debug.apk`

## GitHub (APK baixável)
Push → job `build` → artifact `app-debug-apk`.
Arquivo: `.github/workflows/android-apk.yml`.
