# Flexcity Activation Service

A Spring Boot service that selects the optimal combination of power assets to fulfill activation requests at minimum cost.

## Overview

Given a target date and required power volume (in kW), the service determines which assets to activate while:
- Minimizing total activation cost
- Ensuring total selected volume ≥ requested volume
- Only selecting assets available on the requested date

## Tech Stack

- Java 17
- Spring Boot 3.2
- Maven
- JUnit 5

## Architecture

The project follows a **hexagonal (ports & adapters) architecture**:

```
com.flexcity.activation
├── domain/          # Entities, value objects, exceptions (pure Java)
├── application/     # Use cases and business logic (pure Java)
├── ports/           # Interfaces defining boundaries
├── adapters/        # Spring configuration, repository implementations
└── api/             # REST controllers, DTOs, exception handlers
```

**Why hexagonal?** The domain and application layers are framework-agnostic, making them easy to test without Spring and adaptable if new entry points (CLI, messaging) are added later.

## Key Design Decisions

### Algorithm: Dynamic Programming vs Greedy

The asset selection uses a **dynamic programming approach** (0/1 knapsack variant) instead of a simpler greedy algorithm.

**Why not greedy?**

Greedy (selecting cheapest assets first) doesn't guarantee optimal cost:

| Request: 600 kW | Greedy | Optimal |
|-----------------|--------|---------|
| Asset A: 600 kW @ 150€ | — | ✓ Selected |
| Asset B: 400 kW @ 80€ | ✓ Selected | — |
| Asset C: 300 kW @ 80€ | ✓ Selected | — |
| **Total** | 700 kW @ 160€ | 600 kW @ 150€ |

Greedy overshoots and pays more. DP finds the globally optimal solution.

**Complexity:** O(N × S) where N = number of assets, S = sum of all volumes.

### Exception Handling

| Exception | HTTP Status | Reason |
|-----------|-------------|--------|
| Validation error | 400 | Malformed request |
| `InsufficientCapacityException` | 422 | Valid request, but business rule violation |
| Unexpected error | 500 | Internal error |

## Running the Project

```bash
# Build
mvn clean package

# Run
mvn spring-boot:run

# Run tests
mvn test
```

The service starts on `http://localhost:8080`.

## API Usage

### Endpoint

```
POST /api/activations
Content-Type: application/json
```

### Request

```json
{
  "date": "2024-06-01",
  "requestedVolumeKw": 1000
}
```

### Success Response (200 OK)

```json
{
  "selectedAssets": [
    {
      "assetCode": "SOLAR_01",
      "selectedVolumeKw": 1200,
      "activationCostEur": 45.0
    }
  ]
}
```

### Error Response (422 Unprocessable Entity)

```json
{
  "error": "Insufficient capacity: requested 99999 kW but only 8350 kW available on 2024-06-01"
}
```

### Error Response (400 Bad Request)

```json
{
  "error": "requestedVolumeKw: must be positive"
}
```

## Testing

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AssetSelectionServiceTest
```

Test coverage includes:
- Unit tests for domain validation
- Unit tests for asset selection algorithm
- Integration tests for REST API

## Limitations & Possible Improvements

| Limitation | Potential Improvement |
|------------|----------------------|
| No authentication | Add Spring Security with JWT for API protection |
| Memory usage O(S) for large capacity sums | Add guard to fall back to greedy for S > threshold |
| In-memory repository | Replace with JPA/database adapter |
| No API documentation | Add OpenAPI/Swagger |
| Single date per request | Support date ranges |

