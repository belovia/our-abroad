# Spec Delta

## Purpose

Задаёт, когда система отклоняет запись телефона из‑за уникальности и какой текст видит клиент на маршрутах с телом ответа.

## ADDED Requirements

### Requirement: Duplicate phone is rejected on create
Система MUST отклонять создание учётной записи с телефоном, который уже сохранён у другого пользователя, текстом «Пользователь с таким телефоном уже существует».

#### Scenario: Register with an occupied phone
- **WHEN** клиент вызывает `POST /api/auth/register` с телефоном, который уже есть у другого пользователя
- **THEN** ответ `200`, `success` равен false, сообщение «Пользователь с таким телефоном уже существует», пользователь не создаётся

#### Scenario: Create user with an occupied phone
- **WHEN** клиент вызывает `POST /api/users` с телефоном, который уже есть у другого пользователя
- **THEN** ответ `200`, `success` равен false, сообщение «Пользователь с таким телефоном уже существует», пользователь не создаётся

### Requirement: Duplicate phone is rejected on phone change
Система MUST отклонять смену телефона текущего пользователя на значение, занятое другим пользователем, текстом «Пользователь с таким телефоном уже существует».

#### Scenario: Change phone to an occupied value
- **WHEN** клиент вызывает `PUT /api/users/me/phone` с телефоном другого пользователя
- **THEN** ответ `200`, `success` равен false, сообщение «Пользователь с таким телефоном уже существует», сохранённый телефон не меняется

#### Scenario: Change phone to the same value
- **WHEN** клиент вызывает `PUT /api/users/me/phone` с тем же телефоном, что уже сохранён у него
- **THEN** ответ `200`, `success` равен true, сохранённый телефон остаётся тем же

### Requirement: Duplicate phone is not persisted on partial update
Если в `PATCH /api/users/me` передан `phone`, занятый другим пользователем, система MUST NOT сохранять это значение. Ответ контроллера остаётся `204`.

#### Scenario: Patch me with an occupied phone
- **WHEN** клиент вызывает `PATCH /api/users/me` с полем `phone`, которое уже есть у другого пользователя
- **THEN** ответ `204`, телефон текущего пользователя в хранилище не меняется

#### Scenario: Patch me with the same phone
- **WHEN** клиент вызывает `PATCH /api/users/me` с тем же телефоном, что уже сохранён у него
- **THEN** ответ `204`, телефон в хранилище остаётся тем же

### Requirement: Absent phone skips uniqueness
Система MUST NOT отклонять создание или обновление из‑за уникальности телефона, если телефон не передан или не содержит текста. Формат по-прежнему проверяется только для непустого значения.

#### Scenario: Register without phone
- **WHEN** клиент регистрируется без телефона, а остальные поля допустимы
- **THEN** ответ `200` и `success` равен true

#### Scenario: Clear phone via change endpoint with null
- **WHEN** клиент вызывает `PUT /api/users/me/phone` с `null` в качестве нового телефона
- **THEN** ответ не содержит сообщение «Пользователь с таким телефоном уже существует» из‑за уникальности
