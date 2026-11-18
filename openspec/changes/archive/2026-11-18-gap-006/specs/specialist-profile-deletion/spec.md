# Spec Delta

## Purpose

Даёт владельцу жёстко удалить свой профиль специалиста через API и отклоняет удаление чужого или отсутствующего профиля.

## ADDED Requirements

### Requirement: Owner can delete own specialist profile
Система MUST удалять профиль специалиста текущего пользователя по `DELETE /api/specialist-profiles/{profileId}`, если `profileId` принадлежит этому пользователю.

#### Scenario: Owner deletes own profile
- **WHEN** владелец профиля вызывает `DELETE /api/specialist-profiles/{profileId}` со своим `profileId`
- **THEN** ответ `200`, `success` равен true, строка профиля удалена

### Requirement: Non-owner cannot delete specialist profile
Система MUST отклонять удаление чужого профиля текстом «Доступ запрещён» и MUST не удалять профиль.

#### Scenario: Someone else's profile
- **WHEN** аутентифицированный пользователь вызывает `DELETE /api/specialist-profiles/{profileId}` с `profileId` другого пользователя
- **THEN** ответ `400`, `success` равен false, сообщение «Доступ запрещён», профиль остаётся

### Requirement: Missing or blank profile id is rejected
Система MUST отклонять неизвестный `profileId` текстом «Специалист не найден» и пустой идентификатор текстом «Ошибка валидации запроса», оба с HTTP `400`.

#### Scenario: Unknown profile
- **WHEN** аутентифицированный пользователь вызывает `DELETE /api/specialist-profiles/{profileId}` с несуществующим `profileId`
- **THEN** ответ `400`, `success` равен false, сообщение «Специалист не найден»

#### Scenario: Blank profile id
- **WHEN** сценарий удаления вызывается с пустым или blank `profileId`
- **THEN** ответ `400` (или эквивалентный отказ сценария), `success` равен false, сообщение «Ошибка валидации запроса», удаление не выполняется

### Requirement: Delete endpoint requires authentication
Система MUST требовать access-токен для `DELETE /api/specialist-profiles/{profileId}`.

#### Scenario: Unauthenticated delete
- **WHEN** клиент вызывает `DELETE /api/specialist-profiles/{profileId}` без действительного access-токена
- **THEN** ответ `401`, профиль не удаляется
