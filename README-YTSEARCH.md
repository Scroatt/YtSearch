# Lume Play (MVP + YouTube real)

MVP com identidade própria, agora com dados e player reais do YouTube
via YouTube Data API v3. Sem chave, funciona offline com catálogo local.

## Chave da API (grátis)
1. Google Cloud Console → novo projeto → ative **YouTube Data API v3**.
2. Credenciais → **Chave de API** (restrinja por app se quiser).
3. Local: crie `local.properties` com `YOUTUBE_API_KEY=SUA_CHAVE`
   (o arquivo está no `.gitignore`, nunca é commitado).
4. GitHub: repo → Settings → Secrets → Actions → New secret
   `YOUTUBE_API_KEY` com a chave. O workflow injeta no build.

Cota gratuita: 10.000 unidades/dia. Busca custa 100, em alta custa 1,
detalhes 1, comentários 1. O app usa debounce e cache de sessão.

## Telas
- Início: vídeos em alta no Brasil (ou catálogo local sem chave).
- Busca: resultados reais, sugestões locais, histórico Room, voz.
- Voz: `RecognizerIntent` com permissão e fallback.
- Player: **WebView com player oficial incorporado** para vídeos reais,
  simulado para locais; comentários reais; a seguir.
- Shorts, Biblioteca: histórico, inscrições, curtidos.

## Compilar
`gradle :app:assembleDebug` → `app/build/outputs/apk/debug/app-debug.apk`
Com chave local: `local.properties` resolve sozinho.
