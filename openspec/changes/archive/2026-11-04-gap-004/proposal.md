# Proposal

## Why

В домене уже есть `User.block` / `User.activate` и статусы `BLOCKED` / `DELETED`, а логин любого не-`ACTIVE` отвечает как при неверном пароле. HTTP-сценария смены статуса нет (GAP-004): админ не может заблокировать или разблокировать учётную запись, а клиент заблокированного пользователя не получает отдельный отказ.

## What Changes

- Админские маршруты `PATCH /api/users/{userId}/block` и `PATCH /api/users/{userId}/activate` (`ROLE_ADMIN`): выставляют `BLOCKED` / `ACTIVE` через методы домена.
- При `block` отзываются все refresh-токены пользователя (`RefreshTokenService.revokeAllForUser`). Access доживает свой TTL; фильтр JWT статус не проверяет.
- На `POST /api/auth/login` статус `BLOCKED` даёт новый текст «Учётная запись заблокирована» (`USER_BLOCKED`). Остальные не-`ACTIVE` (включая `DELETED`) по-прежнему «Неверный email или пароль».
- **BREAKING**: логин заблокированного пользователя меняет сообщение с «Неверный email или пароль» на «Учётная запись заблокирована».
- Статус `DELETED` и сценарии его выставления — вне рамок этого изменения.

## Capabilities

### New Capabilities

- `account-blocking`: админская блокировка и активация учётной записи, отзыв сессий при блокировке и отдельный отказ при логине заблокированного.

### Modified Capabilities

- Нет. Существующая спека `phone-uniqueness` к статусу учётной записи не относится.

## Impact

- `ErrorCode`: новый `USER_BLOCKED`.
- `AuthServiceImpl.authenticateByEmailAndPassword`: ветка `BLOCKED` → `USER_BLOCKED`.
- Новые use case'ы `BlockUserUseCase` / `ActivateUserUseCase` и маршруты в `UserController` с `@PreAuthorize("hasRole('ADMIN')")`.
- `RefreshTokenService.revokeAllForUser` вызывается из block; `SecurityConfig` и `JwtAuthenticationFilter` не меняются.
- После реализации: `openspec/docs/ssot/identity.md`, `http-and-errors.md`; GAP-004 закрывается; открывается новый GAP на отсутствие сценария, выставляющего `DELETED`.
