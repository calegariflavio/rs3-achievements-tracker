# RS3 Achievements Tracker

A full-stack web application for tracking RuneScape 3 player achievements, hiscores, and archaeology logs.

> **Live demo:** _coming soon_

---

## Tech Stack

| Layer        | Technology                                      |
|--------------|-------------------------------------------------|
| Backend      | Java 21, Spring Boot 3.3, Spring Security (JWT) |
| Frontend     | React 19, TypeScript, Vite, Tailwind CSS 4      |
| Database     | PostgreSQL 17, Flyway migrations                |
| Cache        | Redis 7                                         |
| Messaging    | Kafka _(planned)_                               |
| Deployment   | Docker, Docker Compose, Nginx                   |

---

## Project Structure

```
rs3-achievements-tracker/
├── backend/          # Spring Boot REST API
├── frontend/         # React SPA
├── docker-compose.yml
├── .env.example
└── README.md
```

---

## Running Locally

### Prerequisites

- Java 21 & Maven 3.9+
- Node.js 20+ & npm
- PostgreSQL 14+ (or use Docker Compose)

---

### Option A — Docker Compose (recommended)

Runs the full stack (frontend, backend, PostgreSQL, Redis) with a single command.

```bash
cp .env.example .env        # fill in POSTGRES_PASSWORD etc.
docker compose up --build
```

| Service  | URL                    |
|----------|------------------------|
| Frontend | http://localhost        |
| Backend  | http://localhost:8080   |
| Postgres | localhost:5432          |
| Redis    | localhost:6379          |

---

### Option B — Manual

#### Backend

```bash
cd backend

# Start a local Postgres instance first, then:
mvn spring-boot:run
```

The API starts on `http://localhost:8080`.

Default local DB config (override via env vars):

| Variable      | Default     |
|---------------|-------------|
| `DB_HOST`     | localhost   |
| `DB_PORT`     | 5432        |
| `DB_NAME`     | fullstackapp|
| `DB_USERNAME` | postgres    |
| `DB_PASSWORD` | (see .env)  |
| `JWT_SECRET`  | (see .env)  |

#### Frontend

```bash
cd frontend
npm install
npm run dev
```

The app starts on `http://localhost:5173` and proxies API calls to `http://localhost:8080`.

---

## Environment Variables

Copy `.env.example` to `.env` and fill in the values:

```env
POSTGRES_DB=appdb
POSTGRES_USER=postgres
POSTGRES_PASSWORD=changeme
```

> **Never commit `.env`** — it is in `.gitignore`.

---

## API Overview

| Method | Endpoint                  | Auth | Description                     |
|--------|---------------------------|------|---------------------------------|
| GET    | `/health`                 |      | Health check                    |
| POST   | `/api/auth/register`      |      | Register account                |
| POST   | `/api/auth/login`         |      | Login — returns JWT             |
| GET    | `/api/me`                 | JWT  | Current user info               |
| POST   | `/api/claim`              | JWT  | Claim a RS3 character           |
| GET    | `/api/player/{name}`      |      | Player profile & hiscores       |
| GET    | `/api/archaeology`        | JWT  | Archaeology log entries         |
| POST   | `/api/archaeology`        | JWT  | Add archaeology entry           |
| DELETE | `/api/archaeology`        | JWT  | Remove archaeology entry        |

---

## Running Tests

```bash
# Backend
cd backend && mvn test

# Frontend (lint)
cd frontend && npm run lint
```

---

## License

MIT
