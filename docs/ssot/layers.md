---
id: layers
title: Слои кода
status: as-is
last_updated: 2026-10-31
---

# Слои

Стек: Java 21, Spring Boot 3.4.3, Spring Security, JDBC, Liquibase, PostgreSQL, JWT (jjwt), springdoc, BCrypt.

Корень пакетов: `ru.belov.ourabroad`.

| Пакет | Роль |
|---|---|
| `web.controller`, `web.dto` | HTTP-адаптер. Контроллер вызывает use case и почти всегда отвечает `200` с телом сценария. |
| `web.validators` | `ErrorCode` и проверки полей. Тексты ошибок — в [http-and-errors.md](http-and-errors.md). |
| `api.usecases` | Сценарий: `*UseCase` и `*UseCaseImpl`. Транзакция ставится на методе сценария, где есть `@Transactional`. |
| `api.usecases.services` | Шаг сценария над доменом и репозиторием. |
| `core.domain`, `core.enums`, `core.security` | Записи, статусы, константы ролей `ROLE_USER` и `ROLE_ADMIN`. |
| `poi.storage` | JDBC-репозитории и SQL. |
| `config.security` | Цепочка фильтров, JWT, текущий пользователь. |

`core.domain.Context` несёт исход сценария. Первый `setError` фиксирует ошибку, повторный вызов её не меняет. Успешный текст — `ErrorCode.SUCCESS` («Успешно»).

Целевая нарезка пакетов из спеки S1 в этот корпус не входит.

## Источник в коде

`pom.xml`, пакеты `web`, `api`, `core`, `poi`, `config`.

## Update Log

| Дата | Изменение |
|---|---|
| 2026-10-31 | Зафиксированы фактические пакеты. |
