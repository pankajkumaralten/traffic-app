# Traffic Light Controller API (Spring Boot + JPA)

A concurrency-safe, validated traffic light controller with persisted intersections, sequences, phases, and history.

## Features
- REST API with proper Spring annotations
- JPA/Hibernate entities for persistence (H2 in-memory)
- Validation: no conflicting GREENs, positive durations
- Async runtime controller per intersection (start/pause/resume/stop)
- History tracking persisted in DB
- DTOs for clean API contracts
- Unit and integration tests

## Run
```bash
mvn clean install
mvn spring-boot:run
