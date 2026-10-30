---
status: approved
approved_at: 2026-09-23
applies_to: [ S1, S2 ]
---

# Skill: implement-from-spec (Identity)

Как реализовать approved спеки S1 + S2 без отхода от контракта.

## Перед кодом

1. Прочитать [S0](../specs/S0-prd.md), [S1](../specs/S1-architecture.md), [S2](../specs/S2-identity.md) — только
   `approved`.
2. Не копировать legacy-пакеты проекта, если они расходятся со S1.
3. Задачи формулировать по [task-template.md](task-template.md).

## Порядок реализации (vertical slices)

1. Domain: `User`, `Profile` + инварианты из S2.
2. Outbound ports + JDBC/Liquibase под эти агрегаты.
3. `PasswordHasher`, `TokenIssuer` (только access), `Clock`.
4. Use cases по одному: RegisterUser → Login → GetMe → UpdateProfile → ChangePassword.
5. REST + springdoc (Authorize Bearer).
6. Прогон AC из S2 вручную через Swagger.

## Правила слоёв (S1)

- Domain без Spring/JDBC.
- Use case оркестрирует порты; транзакция на границе use case.
- Controller только маппит DTO ↔ use case.
- Один accessToken после Login; Register без токенов; нет refresh/logout.

## Запрещено в этой итерации

- Email/phone verification
- Refresh / Logout
- Роли кроме USER
- Upload файлов
- Content / Forum / Marketplace / Trust

## Definition of done

Все acceptance criteria из S2 зелёные; Swagger покрывает register, login, me, me password.
