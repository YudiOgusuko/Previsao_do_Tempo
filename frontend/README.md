# Previsão do Tempo — Frontend

## Estrutura

```
previsao-do-tempo-frontend/
├── index.html
├── css/
│   └── style.css
├── js/
│   ├── icons.js   (ícones SVG por condição do tempo)
│   └── app.js     (chamadas à API, combobox de cidade/dias, renderização)
└── README.md
```

## Como abrir

1. Extraia o zip e abra a pasta `previsao-do-tempo-frontend` no VS Code.
2. Instale a extensão **Live Server** (se ainda não tiver) e clique em "Go Live" com o `index.html` aberto — isso evita problemas de CORS que acontecem ao abrir o arquivo direto com duplo clique (`file://`).
3. Com o backend rodando em `http://localhost:8080`, a página já deve funcionar.

## Ajustes necessários no backend

O frontend espera a base `http://localhost:8080/api/clima` (ajustável no topo de `js/app.js`, na constante `API_BASE`) com estas rotas:

- `GET /api/clima/search?q=texto` → **precisa ser criado** — autocomplete de cidades. Sugestão de implementação usando o `search.json` da WeatherAPI:

  ```java
  public record CitySuggestionDto(String cidade, String estado, String pais) {}

  @GetMapping("/search")
  public ResponseEntity<List<CitySuggestionDto>> search(@RequestParam @NotBlank String q) {
      return ResponseEntity.ok(service.buscarCidades(q));
  }
  // no Service: chame https://api.weatherapi.com/v1/search.json?key=SUA_CHAVE&q={q}
  // e mapeie name -> cidade, region -> estado, country -> pais
  ```

- `GET /api/clima/coordination?cidade=&estado=&pais=` → já existe no seu `WeatherController`. O frontend assume que a resposta tem os campos `latitude`/`longitude` (com fallback para `lat`/`lon`) — ajuste em `selectCity()` dentro de `app.js` se os nomes reais forem diferentes.
- `GET /api/clima?cidade=&dias=` → já existe (`weatherWeek`). O frontend envia `cidade` no formato `"lat,lon"`, que a WeatherAPI aceita, para evitar ambiguidade entre cidades homônimas.
- `GET /api/clima/now?cidade=` → já existe (`weatherNow`), mesma lógica de `lat,lon`.

## Habilitar CORS no Spring Boot

Sem isso o navegador bloqueia as chamadas do frontend para o backend (não tem relação com o DevTools do Spring):

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*") // em produção, restrinja ao domínio real do frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}
```

## Testar sem o backend

Clique em "Buscar" ou "Buscar clima atual" sem o backend rodando: vai aparecer uma mensagem de erro com o botão **"Ver com dados de exemplo"**, que carrega dados fictícios só para validar o layout.
