# Lume Play (MVP)

MVP funcional inspirado no comportamento observável da busca do YouTube
(evidências REA com IDs no chat), com identidade própria: nome, cores e
conteúdo originais. Nenhum código do YouTube foi copiado.

## Telas
- `HomeScreen`: feed com chips de categoria e cards com arte própria.
- `SearchScreen`: `SearchBar` com sugestões (debounce), recentes (Room),
  botão de microfone e resultados.
- `VoiceSearchScreen`: tela dedicada de voz via `RecognizerIntent`, com
  permissão `RECORD_AUDIO` e fallback de erro.
- `ShortsScreen`: pager vertical com curtidas.
- `PlayerScreen`: player simulado com progresso, inscrever-se, curtir,
  descrição expansível e "a seguir".
- `LibraryScreen`: histórico, inscrições, curtidos e downloads.

## Estrutura
- `SearchViewModel`: `StateFlow`, debounce 300ms, histórico Room,
  inscrições e curtidas em memória.
- `data/SearchRepository`: catálogo fake, `suggest()`, `search()`,
  `home()`, `related()`, `byId()`; troque por `VideoSearchApi` (Retrofit).
- `data/local`: Room `search_history` (20 recentes).
- `ui/Components.kt`: `ThumbArt` (arte em gradiente) e `FeedCard`.

## Compilar local
`gradle :app:assembleDebug` → `app/build/outputs/apk/debug/app-debug.apk`

## GitHub (APK baixável)
Push → job `build` → artifact `app-debug-apk`.
Arquivo: `.github/workflows/android-apk.yml`.
