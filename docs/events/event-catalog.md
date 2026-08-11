# Event catalog (planned Kafka contracts)

Kafka is not part of the first runnable milestone. These contracts establish producer ownership before it is introduced.

| Topic | Producer | Consumers | Trigger |
| --- | --- | --- |
| `identity.user-created.v1` | Auth Service | User, Notification, Recommendation | Successful registration |
| `user.profile-updated.v1` | User Service | Recommendation, Search | Profile changed |
| `user.preferences-updated.v1` | User Service | Recommendation | Preferences changed |
| `listing.created.v1` | Listing Service | Recommendation, Search, Notification | Future listing published |

All events include `eventId`, `eventType`, `occurredAt`, `schemaVersion`, `correlationId`, and `payload`. Consumers must be idempotent.
