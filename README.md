# HDFC Life Policy Ledger

## How to run

Run the application with the default `dev` profile:

```sh
./mvnw spring-boot:run
```

The dev profile uses an in-memory H2 database, applies the Flyway migrations, seeds the sample policies, and enables the H2 console at `/h2-console`. Open the API documentation at `/swagger-ui/index.html`.

For PostgreSQL, activate the `prod` profile and provide `DB_URL`, `DB_USER`, and `DB_PASSWORD`:

```sh
SPRING_PROFILES_ACTIVE=prod DB_URL=jdbc:postgresql://localhost:5432/hdfclife DB_USER=your_user DB_PASSWORD=your_password ./mvnw spring-boot:run
```

## Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/policies` | List policies; optional `status`, `type`, or `customer` filter |
| GET | `/api/policies/{policyNo}` | Get one policy |
| GET | `/api/policies/search?minPremium={amount}` | Find policies with at least the minimum premium |
| GET | `/api/policies/{policyNo}/claims` | List claims for a policy |
| POST | `/api/policies` | Create a policy |
| DELETE | `/api/policies/{policyNo}` | Delete a policy |
| POST | `/api/claims` | Submit a claim |

## Policy and rider relationship

`Policy` owns the `policy_riders` many-to-many join table. `Rider` is the inverse side; the relationship uses lazy collections and does not cascade all operations.

## Why the API returns DTOs with open-in-view disabled

DTOs let the service load the response data, including rider codes, inside a transaction and expose only the fields the API intends to return. With open-in-view disabled, a controller may receive a detached `Policy` after its service transaction ends. Serializing a lazy `riders` collection then can trigger a `LazyInitializationException` because there is no active persistence session. Returning entities can also expose persistence details and cause accidental data leakage or recursive serialization. Mapping to a DTO within the service avoids those issues and gives the API a stable response shape.
# masai-assignment-5
