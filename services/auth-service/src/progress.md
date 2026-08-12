# Roommate Platform — Development Progress

> **Project:** Roommate/Rent Sharing Platform
> **Architecture:** Microservices
> **Primary Backend:** Spring Boot
> **Database:** PostgreSQL
> **Progress File:** Updated after each development session

---

# Daily Progress Log

## 📅 August 12, 2026 — Day 1

### Focus

**Auth Service — Architecture & Database Design**

### Completed

* [x] Confirmed microservices architecture.
* [x] Completed creation of the initial service projects:

    * `service-registry`
    * `api-gateway`
    * `auth-service`
    * `user-service`
    * `listing-service`
    * `recommendation-service`
    * `verification-service`
    * `moderation-service`
    * `notification-service`
    * `chat-service`

### Auth Service Architecture

Defined the responsibility of `auth-service`:

* [x] User identity management
* [x] Email/password authentication
* [x] Google OAuth authentication
* [x] Password hashing
* [x] Access token management
* [x] Refresh token management
* [x] Logout/session revocation
* [x] Account status
* [x] Authentication security/auditing

Auth Service will **not** own:

* User profile
* Roommate preferences
* Lifestyle preferences
* Listings
* Recommendations
* Verification documents
* Chat

These belong to their respective services.

---

### User Identity Decisions

* [x] Decided **not to use email as the primary key**.
* [x] Introduced a stable internal `UUID user_id`.
* [x] Email will be a unique account/login identifier.
* [x] Phone number will be stored in `users`.
* [x] Phone numbers will use international/E.164-style representation.
* [x] Phone number will be nullable because Google users may not provide one initially.
* [x] Email and phone number will have database-level uniqueness constraints.

Example:

```text
user_id:      550e8400-e29b-41d4-a716-446655440000
email:        user@example.com
phone_number: +919876543210
```

---

### Authentication Method Design

Instead of putting authentication credentials directly into `users`, we designed:

```text
users
   │
   ▼
auth_identities
   ├── LOCAL
   └── GOOGLE
```

This allows one account to have multiple authentication methods.

Example:

```text
User U123
   │
   ├── Google OAuth
   │
   └── Email + Password
```

Google identity will use Google's stable `sub` value as `provider_user_id`, rather than treating email as the Google identity.

---

### Auth Database Design

Initial database:

```text
auth_db
│
├── users
├── auth_identities
├── refresh_tokens
└── login_attempts
```

#### `users`

Responsible for core account identity:

```text
id
email
phone_number
status
email_verified
phone_verified
created_at
updated_at
```

#### `auth_identities`

Responsible for authentication providers:

```text
id
user_id
provider
provider_user_id
password_hash
created_at
updated_at
```

Supported providers:

```text
LOCAL
GOOGLE
```

#### `refresh_tokens`

Responsible for persistent session/refresh-token management:

```text
id
user_id
token_hash
expires_at
revoked
created_at
```

Raw refresh tokens will **not** be stored.

#### `login_attempts`

Responsible for authentication auditing/security:

```text
id
user_id
identifier
provider
success
ip_address
user_agent
attempted_at
```

---

### Token Architecture

Established the following approach:

```text
Access Token
    ↓
Short-lived JWT
    ↓
Used for normal API requests
```

```text
Refresh Token
    ↓
Long-lived opaque token
    ↓
Used to obtain new access tokens
    ↓
Stored as a hash
    ↓
Can be revoked
```

This allows:

* Short-lived access tokens
* Persistent user sessions
* Logout
* Logout from all devices
* Token revocation
* Multiple device sessions

---

### Database Migration Strategy

Decided to use **Flyway** instead of relying on Hibernate to create/update production schemas.

Migration structure:

```text
src/main/resources/db/migration/

V1__create_users.sql
V2__create_auth_identities.sql
V3__create_refresh_tokens.sql
V4__create_login_attempts.sql
```

Hibernate configuration:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

Responsibilities:

```text
Flyway
    → Database schema creation/migrations

Hibernate
    → Entity ↔ database mapping + validation

PostgreSQL
    → Data storage
```

---

### Configuration

The project uses:

```text
application.properties
```

instead of `application.yml`.

Basic configuration has been planned for:

* Spring application name
* Server port
* PostgreSQL
* JPA/Hibernate
* Flyway
* Eureka Client

Secrets will be supplied through environment variables rather than committed to Git.

---

## Architecture Decisions Made

| Decision                 | Choice                   |
| ------------------------ | ------------------------ |
| Architecture             | Microservices            |
| Auth database            | PostgreSQL               |
| Primary user identifier  | UUID                     |
| Email                    | Unique                   |
| Phone                    | Unique, E.164-style      |
| Password storage         | Secure hash              |
| Authentication providers | Local + Google           |
| Google identifier        | Google `sub`             |
| Access token             | Short-lived JWT          |
| Refresh token            | Long-lived opaque token  |
| Refresh token storage    | Hash only                |
| Schema migration         | Flyway                   |
| Hibernate DDL            | `validate`               |
| Configuration            | `application.properties` |
| Service discovery        | Eureka                   |

---

# Current Status

### Auth Service

```text
Architecture       ✅
Database design     ✅
Security design     ✅
Token strategy      ✅
OAuth strategy      ✅
Migration strategy  ✅

Implementation      ⏳ NOT STARTED
```

---

# Next Session — August 13, 2026

## 🎯 Goal: Start Auth Service Implementation

### First tasks

* [ ] Add Flyway dependency to `pom.xml`
* [ ] Verify PostgreSQL is running
* [ ] Create `auth_db`
* [ ] Configure `application.properties`
* [ ] Create `V1__create_users.sql`
* [ ] Start `auth-service`
* [ ] Verify Flyway migration
* [ ] Verify `users` table creation
* [ ] Create `User` JPA entity
* [ ] Create `UserRepository`

### After that

```text
Database
   ↓
Entity
   ↓
Repository
   ↓
Service Layer
   ↓
Registration API
   ↓
Password Hashing
   ↓
Login
   ↓
JWT
   ↓
Refresh Token
   ↓
Google OAuth
```

---

# Progress History

| Date            | Day | Service      | Focus                          | Status      |
| --------------- | --: | ------------ | ------------------------------ | ----------- |
| August 12, 2026 |   1 | Auth Service | Architecture + DB design       | ✅ Completed |
| August 13, 2026 |   2 | Auth Service | Flyway + PostgreSQL + Entities | ⏳ Next      |

---

## Important Rule for Future Updates

**Do not remove previous daily entries.**

At the end of each development session:

1. Add a new dated section under `Daily Progress Log`.
2. Record what was actually completed.
3. Record important architecture decisions.
4. Record problems encountered and their solutions.
5. Record the next session's tasks.
6. Update the overall progress table.

This file should remain the **single source of truth for development progress**.
