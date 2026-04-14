# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build the project
./mvnw clean install

# Run with dev profile (local PostgreSQL)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Run with Docker (spins up Postgres + app)
docker-compose up --build

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=PublicTests

# Run a single test method
./mvnw test -Dtest=PublicTests#testRegisterUser
```

The app runs on `http://localhost:8080/api`. Swagger UI is at `/api/swagger-ui.html`.

## Architecture Overview

**Spring Boot 3.2 REST API** with a standard layered architecture:

```
Controller → Service → Repository → PostgreSQL (via JPA/Hibernate)
```

- **`controller/`** — REST endpoints. All routes require JWT auth except `/public/**` and `/test/**`.
- **`services/`** — Business logic. `JwtService` handles token generation/validation. `EmailService` sends emails via Spring Mail (Gmail SMTP). `FileService` uploads to Cloudinary. `APIKeyService` manages hashed API keys.
- **`entity/`** — JPA entities mapped to PostgreSQL tables. DDL is managed entirely by **Flyway** (`ddl-auto: none`).
- **`scheduler/`** — `EmailScheduler` scans for `PENDING` emails past their `scheduledTime` (Unix ms timestamp) and dispatches them. The `@Scheduled` cron annotation is currently commented out — the scheduler is triggered manually via `CronController`.
- **`config/`** — `SecurityConfig` wires two auth filters: `JwtAuthFilter` (Bearer token) and `APIKeyAuthFilter` (API key header), both run before `UsernamePasswordAuthenticationFilter`.

## Authentication Flow

Two parallel auth mechanisms supported:
1. **JWT** — Login at `/public/login` → receive access + refresh tokens (also set as HTTP-only cookies). Refresh at `/public/refresh`.
2. **API Keys** — Pass in header; validated by `APIKeyAuthFilter` against hashed values in `api_keys` table.

## Database & Migrations

- Dev profile uses local PostgreSQL at `localhost:5432/syncmate` (user: `catoff`, password: `catoff`).
- Docker Compose maps Postgres to host port `5436`.
- All schema changes go in `src/main/resources/db/migration/` as `V{n}__description.sql`. Flyway runs automatically on startup.

## Active Spring Profiles

| Profile | Usage |
|---|---|
| `dev` | Local development, `localhost:5432` |
| `docker` | Docker Compose networking |
| `prod` | Production (Supabase hosted DB) |

Set with `SPRING_PROFILES_ACTIVE` env var or `-Dspring-boot.run.profiles=`.

## Project Purpose

SyncMate is a **personal cold-email outreach tool** built around a LinkedIn workflow:

1. Browse a LinkedIn profile → Chrome extension parses the page (name, position, gender from pronouns) → saves contact to DB via API key
2. In Hope_Mailer dashboard → schedule emails to contacts using saved templates
3. `EmailScheduler` dispatches pending emails at the right time

Features should only be added if they genuinely simplify a real personal task — not for generic SaaS completeness.

## Full System — Three Codebases

| Project | Path | Role |
|---|---|---|
| SyncMate (this repo) | `~/Documents/SiriusFolder/Java Projects/SyncMate` | Spring Boot REST API |
| Hope_Mailer | `~/Documents/SiriusFolder/Visual-Studio-Code/Hope_Mailer` | Next.js dashboard UI |
| Chrome Extension | `~/Documents/SiriusFolder/Visual-Studio-Code/chrome-extensions/syncmate-chrome-extension-2` | LinkedIn scraper → API |

**Any backend change must be reflected in both Hope_Mailer and the Chrome Extension if they are affected.**

## Chrome Extension

- Vite + React + TypeScript + Shadcn UI
- Parses LinkedIn HTML via `src/utils/parseLinkedIn.ts`
- Calls backend via `src/utils/fetchInstance.ts` using `x-api-key` header (`VITE_API_KEY`) — **not JWT**
- The contact-save endpoint must remain accessible via API key auth

## Frontend (Hope_Mailer)

The companion frontend lives at `~/Documents/SiriusFolder/Visual-Studio-Code/Hope_Mailer` — a **Next.js 14** app (TypeScript, TanStack Query, Shadcn UI).

**When making any backend change, always update the frontend too:**
- New/modified endpoints → update `src/api/` (auth.ts, contact.ts, email.ts, companies.ts, emailTemplates.ts)
- Request/response shape changes → update `src/types.ts`
- New enums → update `src/enums/enums.ts`

Frontend connects to the backend via `NEXT_PUBLIC_BACKEND_URL` (defaults to `http://localhost:8080/api`).

Run frontend: `cd ~/Documents/SiriusFolder/Visual-Studio-Code/Hope_Mailer && npm run dev`

## Key Conventions

- All API responses are wrapped in `MakeResponseDto<?>` with a `message` field and optional `data`.
- Entity-to-DTO mapping uses **ModelMapper** configured in `MapperConfig`.
- `@JsonManagedReference` / `@JsonBackReference` pairs are used on all bidirectional JPA relationships to prevent infinite recursion during serialization — maintain these when adding new relationships.
- `scheduledTime` on `EmailRecord` is stored as a Unix epoch millisecond `Long`, not a `LocalDateTime`.
- File storage uses **Cloudinary** for uploads (images/docs); GCS columns were removed in V2 migration.
