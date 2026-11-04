# account-blocking Specification

## Purpose

Задаёт админскую блокировку и активацию учётной записи, отзыв refresh-сессий при блокировке и отдельный отказ при логине заблокированного пользователя.

## Requirements

### Requirement: Admin can block a user
Система MUST позволять администратору перевести учётную запись в статус `BLOCKED` и при этом отозвать все refresh-токены этого пользователя.

#### Scenario: Admin blocks an active user
- **WHEN** клиент с ролью `ADMIN` вызывает `PATCH /api/users/{userId}/block` для существующего пользователя со статусом `ACTIVE`
- **THEN** ответ `200`, `success` равен true, статус пользователя становится `BLOCKED`, все refresh-записи этого пользователя удалены

#### Scenario: Admin blocks an already blocked user
- **WHEN** клиент с ролью `ADMIN` вызывает `PATCH /api/users/{userId}/block` для пользователя, который уже `BLOCKED`
- **THEN** ответ `200`, `success` равен true, статус остаётся `BLOCKED`

#### Scenario: Block unknown user
- **WHEN** клиент с ролью `ADMIN` вызывает `PATCH /api/users/{userId}/block` с неизвестным `userId`
- **THEN** ответ `200`, `success` равен false, сообщение «Пользователь не найден», статус ничьей учётной записи не меняется

### Requirement: Admin can activate a user
Система MUST позволять администратору перевести учётную запись в статус `ACTIVE`. Активация MUST NOT отзывать refresh-токены.

#### Scenario: Admin activates a blocked user
- **WHEN** клиент с ролью `ADMIN` вызывает `PATCH /api/users/{userId}/activate` для существующего пользователя со статусом `BLOCKED`
- **THEN** ответ `200`, `success` равен true, статус пользователя становится `ACTIVE`

#### Scenario: Admin activates an already active user
- **WHEN** клиент с ролью `ADMIN` вызывает `PATCH /api/users/{userId}/activate` для пользователя, который уже `ACTIVE`
- **THEN** ответ `200`, `success` равен true, статус остаётся `ACTIVE`, refresh-токены не удаляются из-за этого вызова

#### Scenario: Activate unknown user
- **WHEN** клиент с ролью `ADMIN` вызывает `PATCH /api/users/{userId}/activate` с неизвестным `userId`
- **THEN** ответ `200`, `success` равен false, сообщение «Пользователь не найден»

### Requirement: Non-admin cannot change account status
Система MUST отклонять смену статуса учётной записи для вызывающего без роли `ADMIN`.

#### Scenario: Non-admin calls block
- **WHEN** аутентифицированный клиент без роли `ADMIN` вызывает `PATCH /api/users/{userId}/block`
- **THEN** ответ `403`, `code` равен `access_denied`, статус пользователя не меняется

#### Scenario: Non-admin calls activate
- **WHEN** аутентифицированный клиент без роли `ADMIN` вызывает `PATCH /api/users/{userId}/activate`
- **THEN** ответ `403`, `code` равен `access_denied`, статус пользователя не меняется

### Requirement: Blocked user cannot log in
Система MUST отклонять логин пользователя со статусом `BLOCKED` текстом «Учётная запись заблокирована» и MUST NOT выдавать токены и MUST NOT обновлять `lastLoginAt`.

#### Scenario: Login with blocked account
- **WHEN** клиент вызывает `POST /api/auth/login` с верными email и паролем пользователя со статусом `BLOCKED`
- **THEN** ответ `200`, `success` равен false, сообщение «Учётная запись заблокирована», токены не выдаются, `lastLoginAt` не обновляется

#### Scenario: Login with deleted account stays generic
- **WHEN** клиент вызывает `POST /api/auth/login` с верными email и паролем пользователя со статусом `DELETED`
- **THEN** ответ `200`, `success` равен false, сообщение «Неверный email или пароль»

### Requirement: Blocked user cannot refresh after block
После блокировки система MUST отклонять использование ранее выданных refresh-токенов этого пользователя штатным ответом об отозванном или использованном токене.

#### Scenario: Refresh after block
- **WHEN** клиент вызывает `POST /api/auth/refresh` с refresh-токеном пользователя, которого администратор уже заблокировал
- **THEN** ответ содержит сообщение «Refresh-токен уже использован или отозван», новые токены не выдаются
