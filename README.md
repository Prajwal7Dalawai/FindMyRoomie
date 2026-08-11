# FindMyRoomie backend

This repository contains the backend for FindMyRoomie, a trust-first roommate and room-sharing platform for India. It is a Maven monorepo: every directory in `services/` is an independently buildable and deployable Spring Boot service.

## Services

| Service | Port | Owns |
| --- | ---: | --- |
| API Gateway | 8080 | External routing, CORS, rate limiting, request correlation IDs |
| Auth Service | 8081 | Credentials, OTP, tokens, refresh-token sessions |
| User Service | 8082 | Public/private user profiles and roommate preferences |
| Listing Service | 8083 | Properties, rooms, availability, pricing, amenities |
| Recommendation Service | 8084 | Ranked listings and roommate compatibility |
| Verification Service | 8085 | Selfie/document verification workflows |
| Moderation Service | 8086 | Reports, blocks, violations, admin audit actions |
| Notification Service | 8087 | Email, SMS, push, in-app notifications |
| Chat Service | 8088 | Conversations, messages, read status, block enforcement |

Each service owns its database, even during local development. REST handles synchronous operations; Kafka handles asynchronous events. The folders are intentionally scaffolds until we design and implement each service API one at a time.

## Prerequisites

- JDK 17
- Maven 3.9+
- Docker Desktop (for PostgreSQL and Redis)

## Run locally

```powershell
docker compose up -d postgres redis kafka minio
mvn clean verify
```

Copy `.env.example` to `.env` and replace all development secrets before running outside your machine. The compose file creates one database per data-owning service; no service accesses another service's database.

See [docs/architecture/overview.md](docs/architecture/overview.md), [docs/events/event-catalog.md](docs/events/event-catalog.md), and the individual service README files for responsibilities and implementation order.
