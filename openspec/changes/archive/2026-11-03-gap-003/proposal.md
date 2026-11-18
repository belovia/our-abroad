# Proposal

## Why

В таблице `users.phone` стоит UNIQUE, а в `ErrorCode` уже есть текст `PHONE_ALREADY_EXISTS`, но ни один сценарий этот код не ставит. При занятом телефоне клиент либо ловит необработанный конфликт БД, либо получает ответ, который нормой не описан.

## What Changes

- Перед сохранением телефона сценарии проверяют, что значение ещё не занято другим пользователем, и при конфликте ставят `PHONE_ALREADY_EXISTS`.
- Пути: `POST /api/auth/register`, `POST /api/users`, `PUT /api/users/me/phone`, `PATCH /api/users/me` (когда в теле передан `phone`).
- Пустой или отсутствующий телефон по-прежнему необязателен: уникальность для него не проверяется.
- Свой же текущий телефон при смене или PATCH не считается конфликтом.
- **BREAKING**: регистрация, создание пользователя и смена телефона больше не доходят до нарушения UNIQUE; клиент получает зафиксированный отказ с текстом «Пользователь с таким телефоном уже существует» (для маршрутов с телом ответа — `200` и `success: false`).

## Capabilities

### New Capabilities

- `phone-uniqueness`: отказ при попытке записать телефон, который уже есть у другого пользователя.

### Modified Capabilities

- Нет. В `openspec/specs/` возможностей пока нет.

## Impact

- `UserRepository` / `UserService`: проверка существования телефона (с исключением текущего `userId` на обновлении).
- `CreateUserUseCaseImpl`, `AuthenticationUseCaseImpl.register`, `ChangeUserPhoneUseCaseImpl`, `UserUpdateUseCaseImpl`.
- Текст не меняется: `PHONE_ALREADY_EXISTS` («Пользователь с таким телефоном уже существует»).
- `docs/ssot/identity.md` и `docs/ssot/gaps.md`: после реализации GAP-003 закрывается, правило уникальности телефона переносится в тему identity.
