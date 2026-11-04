# Design

## Context

См. proposal.md, раздел Why. В домене уже есть `User.block()` / `User.activate()` и enum `UserStatus` (`ACTIVE`, `BLOCKED`, `DELETED`). HTTP-маршрутов смены статуса нет. `AuthServiceImpl.authenticateByEmailAndPassword` любой не-`ACTIVE` сводит к `INVALID_CREDENTIALS`. `RefreshTokenService.revokeAllForUser` уже есть и нигде из сценариев не вызывается. Единственный образец ролевой проверки — `@PreAuthorize("hasRole('ADMIN')")` на `PATCH /api/reputations/points` в `ReputationController`. `JwtAuthenticationFilter` статус пользователя не читает; access живёт по TTL JWT.

## Goals / Non-Goals

**Goals:**

- Два явных админских маршрута поверх существующих методов домена.
- Отдельный текст логина только для `BLOCKED`.
- При блокировке отозвать refresh-сессии без проверки статуса в JWT-фильтре.

**Non-Goals:**

- Не вводить сценарий выставления `DELETED` и самоудаление.
- Не проверять статус на каждом запросе в `JwtAuthenticationFilter`.
- Не менять `SecurityConfig` (достаточно `@PreAuthorize` + `anyRequest().authenticated()`).
- Не отзывать access-токены досрочно (нет blacklist).

## Decisions

1. **Два PATCH вместо единого `PATCH /status`.** Маршруты `.../block` и `.../activate` совпадают с методами домена и не требуют тела со статусом. Альтернатива с телом `{ "status": "BLOCKED" }` отклонена: больше валидации и расхождения с доменным API.
2. **Ответ `{ userId, success, message }` при `200`.** По образцу `AddReputationPointsUseCase.Response`. Не-админ ловит `403 access_denied` штатным `JwtAccessDeniedHandler` до use case. Неизвестный `userId` → `USER_NOT_FOUND`, `success: false`.
3. **Use case'ы `BlockUserUseCase` / `ActivateUserUseCase`** в `api.usecases.change.user`: `validateId` → `userService.findById` → `user.block()`/`activate()` → `userService.update` → только для block `refreshTokenService.revokeAllForUser`. `@Transactional`. Идемпотентность: повторный block/activate на том же статусе — `success: true` без ошибки.
4. **`ErrorCode.USER_BLOCKED("Учётная запись заблокирована")`.** В `AuthServiceImpl` после нахождения пользователя: `status == BLOCKED` → `USER_BLOCKED`; любой другой не-`ACTIVE` (включая `DELETED`) → `INVALID_CREDENTIALS`. Порядок проверок сохраняется: пустые поля → нет пользователя → статус → пароль. Так клиент узнаёт о блокировке только если email найден; для `DELETED` намеренно оставляем общий текст.
5. **Отзыв только refresh при block.** Access доживает TTL. Альтернатива «проверять статус в фильтре» отклонена: лишний запрос в БД на каждый защищённый вызов. Альтернатива «не отзывать ничего» отклонена пользователем: заблокированный мог бы обновлять сессию 300 дней.
6. **Роль через `@PreAuthorize("hasRole('ADMIN')")` на методах `UserController`.** Как у репутации; отдельное правило в `SecurityConfig` не нужно.

## Risks / Trade-offs

- [Access доживает TTL после block] → Принятый компромисс: refresh уже отозван, новая пара токенов недоступна. Мгновенный отсев потребовал бы проверки статуса в фильтре или blacklist.
- [Логин `BLOCKED` раскрывает существование email] → Сознательное отличие от `INVALID_CREDENTIALS`; зафиксировано в спеке. `DELETED` и неверный пароль по-прежнему общий текст.
- [Нет сценария `DELETED`] → После закрытия GAP-004 открыть новый GAP в `gaps.md`, чтобы пробел не исчез из нормы.

## Migration Plan

Схемы БД нет. Откат — убрать маршруты, use case'ы и ветку `USER_BLOCKED`; уже заблокированные записи остаются `BLOCKED` и снова логинятся общим текстом. Сиды админа вне этого изменения.
