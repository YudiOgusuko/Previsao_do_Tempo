# ⛅ Previsão do Tempo

![Java 17+](https://img.shields.io/badge/Java-17%2B-orange?style=flat-square&logo=openjdk)
![Spring Boot 3.x](https://img.shields.io/badge/Spring_Boot-3.x-green?style=flat-square&logo=springboot)
![Status](https://img.shields.io/badge/Status-Conclu%C3%ADdo-brightgreen?style=flat-square)


Projeto full-stack de consulta de previsão do tempo com foco principal em back-end Java.  
O projeto permite consultar o clima atual ou a previsão dos próximos dias (1 a 14) de qualquer cidade do mundo.

---

## 🎯 Objetivo


Consumo de API externa (WeatherAPI)
Modelagem de DTOs e tratamento de erros de uma API REST
Persistência de dados com Spring Data JPA + PostgreSQL
Containerização com Docker e Docker Compose
Integração entre um back-end Java e um front-end web (HTML/CSS/JS)  

Mesmo com o foco de carreira em back-end, o front-end foi incluído para tornar o projeto mais completo e demonstrável de ponta a ponta.

---

## 🏗️ Arquitetura do Projeto (back-end)

```
src/main/java/br/Previsao_do_Tempo/
├── configuration/   (CorsConfig, RestTemplateConfig)
├── controller/      (WeatherController)
├── dto/             (WeatherDto, WeatherNowDto, CoordinationDto, dadosCoordination, dadosWeather, dadosWeatherNow)
├── handler/         (tratamento de exceções)
├── model/           (Weather, WeatherNow)
├── repository/      (IWeatherRepository, IWeatherNowRepository)
└── sevice/          (WeatherService)
```

---

## 🛠️ Funcionalidades

### ☀️ Consulta e Consumo de API Externa (WeatherAPI)
O back-end consome três recursos da [WeatherAPI](https://www.weatherapi.com/):

| Recurso da WeatherAPI | Uso no projeto |
|---|---|
| `search.json` | Autocomplete de cidades (`/api/clima/search`) |
| `forecast.json` | Previsão de N dias (`/api/clima`) |
| `current.json` (ou `forecast.json` com poucos dias) | Clima atual (`/api/clima/now`) |

É necessário criar uma conta gratuita na WeatherAPI e gerar uma chave de API (ver seção `.env`).

### 🗃️ Gestão e Persistência de Dados
* Cada consulta de previsão semanal e de clima atual é salva no PostgreSQL através do Spring Data JPA.
* Entidades: `Weather` (previsão por dia) e `WeatherNow` (clima atual), persistidas pelos repositórios `IWeatherRepository` e `IWeatherNowRepository`.
* Campos salvos incluem cidade, região, país, temperatura (máxima/mínima ou atual), umidade, descrição, data e dia da semana.
* Isso permite consultar o histórico de buscas já feitas, sem depender de uma nova chamada à WeatherAPI.

## 🌐 Integração com FrontEnd e REST API
O front-end (HTML/CSS/JS puro, na pasta `previsao-do-tempo-frontend/`) se comunica com o back-end por `fetch()`, usando estas rotas:

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/clima/search?q=` | Autocomplete de cidades |
| GET | `/api/clima?cidade=&dias=` | Previsão de N dias |
| GET | `/api/clima/now?cidade=` | Clima atual |
| GET | `/api/clima/all` | Histórico de previsões salvas |
| GET | `/api/clima/all/now` | Histórico de climas atuais salvos |

Como front-end e back-end rodam em origens diferentes durante o desenvolvimento (ex.: `127.0.0.1:5500` e `localhost:8080`), o back-end precisa ter CORS habilitado (classe `CorsConfig`) liberando a origem do front-end.

---
## 🧰 Tecnologias e ferramentas

- **Back-end**: Java, Spring Boot, Spring Web, Spring Data JPA, Bean Validation, RestTemplate (consumo da WeatherAPI)
- **Banco de dados**: PostgreSQL
- **Front-end**: HTML, CSS, JavaScript (sem frameworks)
- **Infraestrutura**: Docker, Docker Compose
- **API externa**: WeatherAP

## 🐋 Docker

O projeto sobe em dois containers via Docker Compose:

- **db**: PostgreSQL, com dados persistidos em um volume Docker.
- **app**: A aplicação Spring Boot (`previsao_do_tempo`), construída a partir do `Dockerfile` do projeto.    

Confira o `docker-compose.yml` do seu projeto para confirmar os nomes exatos dos serviços e as portas mapeadas. Os exemplos abaixo assumem `app` na porta `8080` e `db` na porta `5432`, que são os valores mais comuns.

## ▶️ Como executar (Docker)

1. Clone o repositório.
2. Copie o arquivo `.env.example` para `.env` e preencha os valores (chave da WeatherAPI, credenciais do banco, etc).
3. Na raiz do projeto, rode:
```bash
   docker-compose up --build
```
4. Aguarde os logs mostrarem que a aplicação Spring Boot subiu (procure por `Started PrevisaoDoTempoApplication`).
5. Teste o back-end importando a coleção `previsao-do-tempo.postman_collection.json` no Postman/Insomnia.
6. Abra `previsao-do-tempo-frontend/index.html` com a extensão **Live Server** do VS Code para usar a interface completa.
   Para rodar em segundo plano: `docker-compose up --build -d`. Para parar: `docker-compose down` (adicione `-v` para também apagar o volume do banco).

### ❌ Erro "port is already allocated"

Se ao rodar `docker-compose up` aparecer um erro como:

```
Error response from daemon: driver failed programming external connectivity on endpoint ...: Bind for 0.0.0.0:8080 failed: port is already allocated
```

Isso significa que outro processo (ou outro container) já está usando a porta `8080` (aplicação) ou `5432` (PostgreSQL) na sua máquina. Duas formas de resolver:

**🔸Opção 1 — liberar a porta**
Descubra o que está usando a porta e finalize o processo:
- Windows (PowerShell): `netstat -ano | findstr :8080` (ou `:5432`), depois `taskkill /PID <pid> /F`
- Linux/Mac: `lsof -i :8080` (ou `:5432`), depois `kill -9 <pid>`
- Se for outro container Docker: `docker ps` para achar o nome, depois `docker stop <nome>`  

**🔸Opção 2 — mudar a porta usada localmente**
  No `docker-compose.yml`, altere apenas o lado esquerdo do mapeamento de portas (a porta do seu computador; a da direita, dentro do container, não precisa mudar):
```yaml
ports:
  - "8081:8080"   # app passa a responder em localhost:8081
  - "5433:5432"   # postgres passa a responder em localhost:5433
```
Se mudar a porta do `app`, lembre de atualizar `API_BASE` em `previsao-do-tempo-frontend/js/app.js` para apontar para a nova porta.
