# Roommate Platform — Engineering Plan

## 1. Product Vision

Build a production-oriented roommate/flat-sharing platform for India that helps users:

- Find compatible roommates.
- Find rooms/flats and shared accommodation.
- Split rent and shared expenses.
- Discover trustworthy users and properties.
- Reduce scams, fake profiles, harassment, and unsafe interactions.
- Match users based on budget, location, lifestyle, and preferences.

The core differentiator should be **trust + compatibility**, not simply another rental listing website.

---

# 2. Core Problems to Solve

## User-side problems

1. Fake profiles
2. Fake property listings
3. Rental/deposit scams
4. Catfishing
5. Dangerous or abusive users
6. Harassment and spam
7. Lifestyle mismatch between roommates
8. Hidden rental/maintenance charges
9. Ghosting / inactive listings
10. Unsafe in-person meetings
11. Fake reviews
12. Difficulty finding compatible roommates
13. Difficulty finding trustworthy property owners/tenants

## Product goals

- Make identity and listing trust visible.
- Match roommates based on compatibility.
- Reduce fraud before users transact.
- Make reporting/blocking easy.
- Keep sensitive information secure.
- Provide a strong moderation/admin system.

---

# 3. MVP Scope

Do NOT build every feature immediately.

## MVP

### Authentication
- Phone OTP
- Email verification
- Password authentication
- JWT access token
- Refresh token
- Logout
- Device/session management
- Basic rate limiting

### User Profile
- Name
- Profile photo
- Age
- Gender
- Occupation
- College/company
- City
- Bio
- Budget
- Move-in date

### Roommate Preferences
- Minimum/maximum budget
- Preferred location
- Food preference
- Smoking preference
- Alcohol preference
- Pets
- Sleep schedule
- Work/study schedule
- Cleanliness preference
- Guests
- Cooking
- AC usage
- Language
- Other lifestyle preferences

### Listings
- Create listing
- Edit listing
- Delete listing
- Upload property images
- Rent
- Deposit
- Maintenance
- Utilities
- Brokerage
- Parking
- Lock-in period
- Notice period
- Available date
- Location
- Amenities

### Search
Initially use PostgreSQL queries:
- City
- Area
- Budget
- Room type
- Availability
- Gender preference
- Amenities

### Trust
- Phone verified
- Email verified
- Profile completeness
- Verification status
- User reports
- Reviews

### Safety
- Block user
- Report user
- Report listing
- Admin moderation

---

# 4. Future Features

## Identity Verification

Potential verification levels:

### Level 1
- Phone verified
- Email verified

### Level 2
- Government ID verification
- Selfie verification
- Liveness detection

### Level 3
- Government/KYC provider verification
- Additional checks where legally permitted

Potential commercial providers to evaluate later:
- Signzy
- HyperVerge
- IDfy
- AuthBridge

Important:
- Do not assume direct access to government databases.
- Aadhaar verification is regulated.
- Use authorized providers and follow applicable UIDAI and Indian privacy requirements.
- Do not store Aadhaar/government documents unnecessarily.
- Encrypt sensitive information.
- Define retention/deletion policies.

For MVP, use:
- Phone OTP
- Email
- Selfie
- Optional document upload
- AI-assisted checks
- Manual admin review

Do not claim that OCR/face matching equals official government verification.

---

# 5. Trust Score

Potential trust score components:

- Phone verified
- Email verified
- Government ID verified
- Face verified
- Student/company email verified
- Account age
- Successful roommate history
- Positive reviews
- Number/severity of reports
- Policy violations
- Listing verification

Do not make the score a hidden or arbitrary "criminal score".

Users should see meaningful verification badges and clear explanations.

---

# 6. Safety & Security

## Authentication Security

- Spring Security
- Password hashing using Argon2 or BCrypt
- Short-lived JWT access tokens
- Refresh tokens
- Refresh token rotation
- Token revocation
- MFA where appropriate
- Device/session management
- Login alerts

## API Security

- Input validation
- DTO validation
- Authorization on every protected endpoint
- RBAC
- Rate limiting
- IP throttling
- CAPTCHA for suspicious activity
- WAF at deployment
- CORS configuration
- Secure HTTP headers
- Request size limits
- File upload validation

## File Security

For profile/property/identity documents:

- Validate MIME type
- Validate file extension
- Restrict file size
- Virus/malware scanning
- Strip EXIF metadata when appropriate
- Store files outside application servers
- Use private object-storage buckets
- Signed URLs for controlled access
- Encryption at rest
- Encryption in transit

## Sensitive Data

For government IDs and personal information:

- Minimize collection
- Encrypt sensitive fields
- Strict access controls
- Admin audit logs
- Never log sensitive documents or tokens
- Never expose government ID numbers in APIs unnecessarily
- Data retention/deletion policies
- Least privilege
- Secret management

## Abuse Prevention

- Block users
- Report users
- Report listings
- Spam detection
- Profanity/toxicity detection
- Threat detection
- Malicious-link detection
- Image moderation
- Duplicate/fake-account detection
- Suspicious activity monitoring

---

# 7. Database Strategy

Use **polyglot persistence only where justified**.

## Primary Database — PostgreSQL

Use PostgreSQL as the source of truth for:

- Users
- Profiles
- Preferences
- Listings
- Applications
- Reviews
- Reports
- Verification records
- Payments later
- Moderation records

Why:
- ACID
- Transactions
- Foreign keys
- Constraints
- Complex queries
- Strong consistency
- Good indexing

## Redis

Use Redis for:

- OTPs
- Temporary session data
- Rate limiting
- Caching
- Online status
- Short-lived data
- Distributed locks where needed

Do not use Redis as the primary source of truth.

## Recommendation Data

The Recommendation Service should maintain its own recommendation-oriented data/features instead of directly joining other services' databases.

Potential data:
- User preference features
- Listing feature vectors
- Owner/roommate requirement features
- Behavioral aggregates
- Recommendation scores
- Recent interaction history
- Candidate/ranking metadata

Redis can be used for:
- Frequently requested recommendations
- Short-lived recommendation caches
- Recent behavioral counters
- Fast feature retrieval

PostgreSQL can be used for durable recommendation data where appropriate.

## Object Storage

Use:
- AWS S3
- Cloudflare R2
- MinIO for local development/self-hosted environments

Store:
- Property images
- Profile images
- Verification documents
- Rental agreements

Do NOT store large binary files directly in PostgreSQL.

## OpenSearch

Introduce later when search complexity/scale requires it.

Use for:
- Full-text search
- Location/area search
- Property filters
- Keyword search
- Ranking

PostgreSQL remains the source of truth.

## MongoDB

Optional.

Do not introduce MongoDB merely because this is a microservices project.

Potential future use:
- High-volume chat/message storage

However, start chat with PostgreSQL and introduce MongoDB only when the workload justifies it.

---

# 8. Microservices Architecture

## Initial services

### API Gateway
Responsibilities:
- Routing
- Authentication forwarding
- Rate limiting
- Request correlation ID
- CORS
- Central entry point

### Auth Service
Responsibilities:
- Registration
- Login
- Password hashing
- OTP
- JWT
- Refresh tokens
- Session management

### User Service
Responsibilities:
- User profile
- Preferences
- Profile completion
- User status

### Listing Service
Responsibilities:
- Properties
- Rooms
- Availability
- Pricing
- Amenities
- Listing lifecycle

### Recommendation Service
Responsibilities:
- Personalized room recommendations
- Roommate compatibility scoring
- Owner/existing-roommate preference matching
- Behavioral signal processing
- Candidate generation and ranking
- Recommendation explanations
- Recommendation feature storage

Important:
The Recommendation Service should be a first-class core service, not merely a search/filter feature.

It should combine:
1. Explicit user preferences.
2. Listing requirements.
3. Owner/existing-roommate desired behavior.
4. User behavioral signals.
5. Availability.
6. Trust and safety signals.

Do not make the recommendation service directly query every other service's database. Prefer Kafka events and service-owned recommendation data/features.

### Verification Service
Responsibilities:
- Identity verification
- Selfie verification
- Document workflow
- Verification status
- Provider integration later

### Matching Service
Responsibilities:
- Compatibility calculation
- Candidate ranking
- Recommendation

### Review Service
Responsibilities:
- Reviews
- Ratings
- Verified stay/roommate relationships

### Report/Moderation Service
Responsibilities:
- Reports
- User violations
- Listing violations
- Moderation workflow
- Admin actions

### Notification Service
Responsibilities:
- Email
- SMS
- Push notifications
- In-app notifications

### Chat Service
Responsibilities:
- Conversations
- Messages
- Read status
- Block restrictions
- Moderation hooks

---

# 9. Database Ownership

IMPORTANT MICROservices rule:

Do NOT allow every service to directly access every service's tables.

Bad:

Auth + User + Listing
        |
        v
Same tables

This creates a distributed monolith.

Preferred:

Auth Service -> auth_db
User Service -> user_db
Listing Service -> listing_db
Verification Service -> verification_db
Review Service -> review_db

Services communicate through:
- REST APIs for synchronous operations
- Kafka events for asynchronous operations

During development, these databases can still run inside one PostgreSQL server/cluster.

The architectural rule is:
**each service owns its data.**

---

# 10. Event-Driven Architecture

Use Apache Kafka for asynchronous communication.

Example:

User Service
    |
    | UserCreated
    v
  Kafka
    |
    +----> Notification Service
    |
    +----> Matching Service
    |
    +----> Analytics later

Possible events:

- UserCreated
- UserUpdated
- UserPreferenceUpdated
- ListingCreated
- ListingUpdated
- ListingRemoved
- ListingViewed
- ListingSaved
- ListingShared
- ListingContacted
- ListingIgnored
- ListingReported
- SearchPerformed
- FilterApplied
- RoommateProfileViewed
- RoommateLiked
- RoommateRejected
- ListingUpdated
- ListingRemoved
- UserVerified
- VerificationFailed
- RoommateMatchCreated
- ReviewCreated
- UserReported
- ListingReported
- UserBlocked
- MessageSent
- NotificationRequested

Use events when another service does not need to respond immediately.

Do not turn every REST call into Kafka.

---

# 11. Recommended Tech Stack

## Backend

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- Bean Validation
- Spring Cloud Gateway
- Spring Kafka
- WebSocket/STOMP where appropriate

## Databases

- PostgreSQL
- Redis
- OpenSearch later
- Object storage (S3/MinIO)

## Messaging

- Apache Kafka

## Infrastructure

- Docker
- Docker Compose for local development
- Kubernetes later
- Nginx/load balancer where appropriate

## Observability

- Prometheus
- Grafana
- Centralized logging
- Distributed tracing
- OpenTelemetry later

## Frontend

- React initially
- Mobile app later if product validates

---

# 12. Suggested Project Structure

Repository:

roommate-platform/

    services/
        api-gateway/
        auth-service/
        user-service/
        listing-service/
        verification-service/
        matching-service/
        review-service/
        moderation-service/
        notification-service/
        chat-service/

    infrastructure/
        docker/
        kafka/
        postgres/
        redis/
        opensearch/
        minio/

    docs/
        architecture/
        api/
        security/

    docker-compose.yml

Later consider separate repositories if team size and deployment workflow justify it.

---

# 13. Initial Database Entities

## User

- id
- email
- phone
- password_hash
- status
- created_at
- updated_at

## Profile

- id
- user_id
- name
- date_of_birth/age where legally and product-wise appropriate
- gender
- occupation
- company/college
- city
- bio
- profile_image

## Preferences

- user_id
- min_budget
- max_budget
- preferred_locations
- food_preference
- smoking
- alcohol
- pets
- sleep_schedule
- work_schedule
- cleanliness
- guests
- cooking
- other preferences

## Listing

- id
- owner_id
- title
- description
- city
- area
- latitude/longitude as appropriate
- rent
- deposit
- maintenance
- brokerage
- available_from
- room_type
- status
- created_at

## Verification

- id
- user_id
- verification_type
- provider
- status
- verified_at
- reference_id
- storage_object_id where applicable

Never store raw government documents unless there is a justified legal/product need.

---

# 14. Recommendation System

Recommendation is a core product capability.

The objective is not simply:

User -> Listing

It should model:

User preferences
        +
Listing requirements
        +
Owner/existing-roommate preferences
        +
User behavior
        +
Availability
        +
Trust/safety signals
        ↓
Recommendation Service
        ↓
Ranked listings / compatible roommates

## V1 — Rule-Based Recommendation

Do NOT start with machine learning.

Use a transparent scoring system.

Example:

Location compatibility       25%
Budget compatibility         20%
Lifestyle compatibility      20%
Owner/roommate match         15%
User behavior                10%
Availability                  5%
Trust signals                 5%
                             ----
                             100%

Example:

User:
- Budget: ₹10k–₹14k
- Whitefield
- Non-smoker
- Working professional
- Quiet environment

Listing:
- ₹12k
- Whitefield
- Non-smoking preferred
- Working professional preferred
- Quiet home

The recommendation service produces a high compatibility score.

The UI should explain the recommendation rather than showing only a number:

92% Match

✓ Within your budget
✓ Matches preferred location
✓ Non-smoking
✓ Similar lifestyle
✓ Owner preference matches
✓ Available around your move-in date

## Behavioral Recommendation

Track meaningful user interactions:

- ListingViewed
- ListingClicked
- ListingSaved
- ListingShared
- ListingContacted
- ListingIgnored
- ListingReported
- SearchPerformed
- FilterApplied
- RoommateProfileViewed
- RoommateLiked
- RoommateRejected

Example event:

{
  "event": "ListingViewed",
  "userId": "123",
  "listingId": "987",
  "timestamp": "...",
  "duration": 42
}

Events should be published through Kafka.

Example:

User
  ↓
Listing Viewed
  ↓
Kafka
  ↓
Recommendation Service
  ↓
Behavior Profile / Features

Behavior can reveal preferences the user never explicitly entered.

For example:
- Frequently views Whitefield listings.
- Frequently views ₹10k–₹14k listings.
- Saves 2BHK listings.
- Ignores PG listings.
- Contacts non-smoking listings.
- Spends more time on listings near metro stations.

The system can gradually infer these preferences.

## Owner / Existing-Roommate Requirements

The recommendation system must model both sides.

User wants:
- Budget
- Location
- Lifestyle
- Room type
- Move-in date

Owner/existing roommates want:
- Non-smoker
- Working professional
- Student
- Quiet person
- Guest preferences
- Food preferences
- Pets
- Sleep schedule

A good recommendation requires compatibility between both sides.

## Candidate Generation and Ranking

As the system grows:

1. Candidate generation
2. Ranking
3. Recommendation explanation

Example:

OpenSearch/PostgreSQL
        ↓
500 potentially relevant listings
        ↓
Recommendation Service
        ↓
Ranking
        ↓
Top 20 listings
        ↓
User

Do not ask the ML model to search the entire database.

## V2 — Data-Driven Ranking

After collecting enough quality interaction data:

Behavior Events
        ↓
Feature Engineering
        ↓
Recommendation Model
        ↓
Candidate Ranking

Possible future features:
- Click-through rate
- Save probability
- Contact probability
- Match acceptance
- Successful roommate interaction
- Listing freshness
- Distance
- Price deviation from preferred budget
- Lifestyle similarity

ML should be introduced only when there is enough reliable data and a measurable ranking problem.

## Recommendation Architecture

Do NOT do this:

Recommendation Service
    ├── user_db
    ├── listing_db
    ├── review_db
    ├── verification_db
    └── chat_db

Instead:

User Service
    ↓ UserPreferenceUpdated
Kafka
    ↓
Recommendation Service

Listing Service
    ↓ ListingCreated / ListingUpdated
Kafka
    ↓
Recommendation Service

Behavior Tracking
    ↓ ListingViewed / ListingSaved / etc.
Kafka
    ↓
Recommendation Service

Recommendation Service owns its recommendation-oriented data/features.

This keeps service boundaries clean and prevents the recommendation system from becoming tightly coupled to every database.

---

# 15. Search Architecture

## MVP

Use PostgreSQL.

Example filters:
- City
- Area
- Budget
- Room type
- Available date
- Amenities

## Later

Add OpenSearch:

PostgreSQL
    |
    | source of truth
    v
OpenSearch
    |
    | search/filter/ranking
    v
Frontend

Keep OpenSearch eventually consistent with PostgreSQL.

Use Kafka events or an indexing pipeline to update search indexes.

---

# 16. Chat Architecture

Start simple:

- PostgreSQL
- WebSocket
- Redis if needed for presence
- Kafka for asynchronous notification/moderation events

Future:

- MongoDB or another message-oriented store if volume justifies it.
- Message moderation pipeline.
- Attachment storage in object storage.

Do not introduce MongoDB just to demonstrate multiple databases.

---

# 17. Admin Dashboard

Admin capabilities:

- View users
- Verify users
- Verify listings
- Review reports
- Suspend users
- Ban users
- Remove listings
- Review verification failures
- Review suspicious activity
- View moderation history
- Audit admin actions

Every sensitive admin action should be auditable.

Example:

admin_user_id
action
target_type
target_id
reason
timestamp

---

# 18. Observability

Every service should eventually support:

## Logs
- Structured JSON logs
- Correlation ID
- Request ID
- Service name
- Timestamp
- Log level

Never log:
- Passwords
- JWTs
- OTPs
- Government IDs
- Sensitive document contents

## Metrics

Track:
- Request count
- Error rate
- Latency
- Database connection pool
- Kafka consumer lag
- Redis hit/miss
- Authentication failures
- Verification success/failure
- Report volume

## Tracing

Use:
- OpenTelemetry
- Trace IDs across services

Example:

Frontend
 -> API Gateway
 -> Listing Service
 -> PostgreSQL

All should be traceable through one request ID/trace ID.

---

# 19. Deployment Strategy

## Local Development

Docker Compose:

- PostgreSQL
- Redis
- Kafka
- Kafka UI
- MinIO
- Services

## Stage 1

Deploy application to one cloud environment.

Use:
- Docker
- Managed PostgreSQL
- Managed Redis if available
- Object storage
- Kafka managed/self-hosted depending on cost

## Stage 2

Introduce:
- Kubernetes
- Horizontal scaling
- Load balancing
- Autoscaling
- CI/CD

Do not introduce Kubernetes before you understand the application and service boundaries.

---

# 20. CI/CD

Use GitHub Actions or another CI system.

Pipeline:

Pull Request
    |
    v
Compile
    |
    v
Unit Tests
    |
    v
Integration Tests
    |
    v
Static Analysis
    |
    v
Build Docker Image
    |
    v
Security Scan
    |
    v
Deploy

Use:
- Unit tests
- Integration tests
- Testcontainers for PostgreSQL/Kafka/Redis where appropriate
- Docker image scanning
- Dependency vulnerability scanning

---

# 21. Testing Strategy

## Unit Tests

Test:
- Business rules
- Matching algorithm
- Validation
- Authorization rules

## Integration Tests

Test:
- PostgreSQL
- Redis
- Kafka
- Spring Security
- Repository behavior

Use Testcontainers where possible.

## API Tests

Test:
- Authentication
- Authorization
- Listing CRUD
- Search
- Reports
- Verification workflows

## Security Tests

Test:
- Broken authorization
- IDOR
- Rate limits
- Invalid JWT
- Expired JWT
- Malicious file uploads
- Injection attacks
- Privilege escalation

---

# 22. Development Roadmap

## Phase 0 — Architecture

Before coding:

- Define requirements
- Define bounded contexts
- Define service boundaries
- Define APIs
- Define database ownership
- Define Kafka events
- Define security model
- Create architecture diagrams
- Create ADRs for important decisions

Deliverables:
- Architecture diagram
- ER diagrams
- API contract
- Event catalog

---

## Phase 1 — Foundation

Build:

- API Gateway
- Auth Service
- User Service
- PostgreSQL
- Redis
- Docker Compose
- Spring Security
- JWT
- Basic logging

Goal:
User can register/login and maintain profile.

---

## Phase 2 — Listings

Build:

- Listing Service
- Property CRUD
- Images
- Search/filter
- PostgreSQL indexes
- Object storage

Goal:
Users can create and find rooms.

---

## Phase 3 — Roommate Preferences

Build:

- Preference profiles
- Compatibility algorithm
- Matching Service
- Basic recommendations

Goal:
Users can discover compatible roommates.

---

## Phase 4 — Kafka

Introduce:

- Kafka
- Event-driven notifications
- UserCreated event
- ListingCreated event
- UserVerified event
- ReportCreated event

Goal:
Understand asynchronous communication and eventual consistency.

---

## Phase 5 — Trust & Safety

Build:

- Verification Service
- Selfie verification
- Government ID workflow
- Verification provider abstraction
- Trust badges
- Reports
- Blocking
- Moderation
- Admin dashboard

Goal:
Make safety a core product capability.

---

## Phase 6 — Chat

Build:

- WebSocket
- Conversations
- Messages
- Read receipts
- Online status
- Block enforcement
- Message moderation

Goal:
Allow users to communicate safely.

---

## Phase 7 — Search

Introduce OpenSearch when PostgreSQL search becomes insufficient.

Build:
- Full-text search
- Geo search
- Ranking
- Advanced filtering

---

## Phase 8 — Production Hardening

Add:

- Prometheus
- Grafana
- OpenTelemetry
- Centralized logs
- Distributed tracing
- Rate limiting
- WAF
- Secrets management
- CI/CD
- Security scans
- Backup/recovery
- Disaster recovery plan

---

## Phase 9 — Scale

Only after usage justifies it:

- Kubernetes
- Horizontal scaling
- Service autoscaling
- Managed Kafka
- Read replicas
- Database partitioning where needed
- CDN
- Advanced caching
- Search cluster scaling

---

# 23. Important Engineering Principles

1. Do not over-engineer the MVP.
2. Microservices should have clear business boundaries.
3. Each service owns its data.
4. PostgreSQL is the initial source of truth.
5. Redis is for speed/temporary state, not core persistence.
6. Kafka is for asynchronous events, not every communication.
7. Object storage is for files.
8. Add MongoDB/OpenSearch only when justified.
9. Security must be designed from day one.
10. Never store sensitive data unnecessarily.
11. Never trust frontend authorization.
12. Validate and authorize on the backend.
13. Every admin action should be auditable.
14. Use observability before production scale.
15. Prefer managed infrastructure when operating a real product.
16. Use ADRs for significant architecture decisions.
17. Measure actual bottlenecks before optimizing.

---

# 24. Portfolio / Interview Value

This project should demonstrate:

### Backend
- Java
- Spring Boot
- Spring Security
- REST APIs
- WebSockets
- JPA/Hibernate

### Databases
- PostgreSQL
- Redis
- OpenSearch
- Object storage

### Distributed Systems
- Microservices
- Kafka
- Event-driven architecture
- Eventual consistency
- Service-to-service communication
- Distributed tracing
- Recommendation pipelines
- Candidate generation and ranking

### Security
- JWT
- RBAC
- Rate limiting
- Secure file uploads
- Identity verification
- Audit logs
- Data protection

### DevOps
- Docker
- Docker Compose
- CI/CD
- Monitoring
- Logging
- Kubernetes later

This should be presented as a production-oriented system rather than a simple CRUD application.

---

# 25. Immediate Next Steps

When continuing this project, follow this order:

1. Finalize product requirements.
2. Define bounded contexts.
3. Design microservice boundaries.
4. Design database ownership.
5. Design PostgreSQL schemas.
6. Design REST APIs.
7. Design Kafka topics/events.
8. Create architecture diagram.
9. Set up monorepo and Docker Compose.
10. Build Auth Service.
11. Build User Service.
12. Build Listing Service.
13. Add Redis.
14. Add Kafka.
15. Add Recommendation Service with transparent rule-based scoring.
16. Add behavioral event tracking.
17. Add Verification Service.
17. Add Moderation and safety.
18. Add Chat.
19. Add OpenSearch.
20. Add observability.
21. Add CI/CD.
22. Production hardening.
23. Cloud deployment.
24. Load testing.
25. Security testing.

---

# 26. Current Recommended Stack

Use this as the baseline unless a future architectural decision changes it:

Frontend:
- React

Backend:
- Java
- Spring Boot
- Spring Security
- Spring Cloud Gateway
- Spring Data JPA
- Spring Kafka
- WebSocket/STOMP
- Recommendation/ranking service

Data:
- PostgreSQL
- Redis
- S3/MinIO
- OpenSearch later
- MongoDB only if justified

Messaging:
- Apache Kafka

Infrastructure:
- Docker
- Docker Compose
- Kubernetes later

Observability:
- Prometheus
- Grafana
- OpenTelemetry
- Centralized logging

Testing:
- JUnit
- Mockito
- Testcontainers
- API/integration/security testing

---

# 27. Guiding Principle

The goal is NOT:

"Build a roommate CRUD app using many technologies."

The goal is:

"Build a trustworthy roommate marketplace that demonstrates real-world backend engineering, distributed systems, security, scalability, observability, and product thinking."

Every technology decision should support that goal.
