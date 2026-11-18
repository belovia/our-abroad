# Spec Delta

## Purpose

Задаёт, кто и когда может оставить отзыв о специалисте, как из отзывов складываются `rating` и `reviewsCount` профиля и как отзывы читаются.

## ADDED Requirements

### Requirement: Client can review a completed visit
Система MUST принимать отзыв текущего пользователя по его записи, если запись `CONFIRMED`, её `startTime` уже наступил и отзыва по ней ещё нет. Отзыв содержит оценку 1–5 и необязательный текст.

#### Scenario: Review after confirmed past visit
- **WHEN** клиент вызывает `POST /api/specialist-reviews` с `bookingId` своей записи со статусом `CONFIRMED`, `startTime` в прошлом, `score` = 5 и текстом
- **THEN** ответ `200`, `success` равен true, отзыв сохранён с `authorId` текущего пользователя и `specialistId` записи

#### Scenario: Review without text
- **WHEN** клиент вызывает `POST /api/specialist-reviews` с допустимой записью и `score` = 4 без `text`
- **THEN** ответ `200`, `success` равен true, отзыв сохранён с пустым текстом

### Requirement: Review is rejected without a completed visit
Система MUST отклонять отзыв текстом «Отзыв можно оставить только после подтверждённого визита», если запись не `CONFIRMED` или её `startTime` ещё не наступил.

#### Scenario: Booking is pending
- **WHEN** клиент вызывает `POST /api/specialist-reviews` по своей записи со статусом `PENDING`
- **THEN** ответ `200`, `success` равен false, сообщение «Отзыв можно оставить только после подтверждённого визита», отзыв не сохраняется

#### Scenario: Booking is cancelled
- **WHEN** клиент вызывает `POST /api/specialist-reviews` по своей записи со статусом `CANCELLED`
- **THEN** ответ `200`, `success` равен false, сообщение «Отзыв можно оставить только после подтверждённого визита»

#### Scenario: Visit is in the future
- **WHEN** клиент вызывает `POST /api/specialist-reviews` по своей записи `CONFIRMED`, у которой `startTime` ещё не наступил
- **THEN** ответ `200`, `success` равен false, сообщение «Отзыв можно оставить только после подтверждённого визита»

### Requirement: Only the booking owner can review
Система MUST отклонять отзыв по чужой записи текстом «Доступ запрещён» и MUST отвечать «Запись не найдена» для неизвестного `bookingId`.

#### Scenario: Someone else's booking
- **WHEN** клиент вызывает `POST /api/specialist-reviews` с `bookingId` записи другого пользователя
- **THEN** ответ `200`, `success` равен false, сообщение «Доступ запрещён»

#### Scenario: Unknown booking
- **WHEN** клиент вызывает `POST /api/specialist-reviews` с несуществующим `bookingId`
- **THEN** ответ `200`, `success` равен false, сообщение «Запись не найдена»

### Requirement: One review per booking
Система MUST отклонять второй отзыв по той же записи текстом «Отзыв по этой записи уже оставлен».

#### Scenario: Second review for the same booking
- **WHEN** клиент повторно вызывает `POST /api/specialist-reviews` с `bookingId`, по которому отзыв уже сохранён
- **THEN** ответ `200`, `success` равен false, сообщение «Отзыв по этой записи уже оставлен», рейтинг профиля не меняется

### Requirement: Score is validated
Система MUST отклонять оценку вне диапазона 1–5 текстом «Оценка должна быть от 1 до 5» и MUST отвечать «Ошибка валидации запроса» на пустой `bookingId`.

#### Scenario: Score out of range
- **WHEN** клиент вызывает `POST /api/specialist-reviews` со `score` = 0 или 6
- **THEN** ответ `200`, `success` равен false, сообщение «Оценка должна быть от 1 до 5», отзыв не сохраняется

#### Scenario: Missing bookingId
- **WHEN** клиент вызывает `POST /api/specialist-reviews` без `bookingId`
- **THEN** ответ `200`, `success` равен false, сообщение «Ошибка валидации запроса»

### Requirement: Rating and review count are recalculated
После сохранения отзыва система MUST обновить у профиля специалиста `reviewsCount` на число отзывов профиля и `rating` на среднее арифметическое их оценок.

#### Scenario: First review
- **WHEN** у профиля `rating` = 0 и `reviewsCount` = 0 и клиент сохраняет отзыв со `score` = 4
- **THEN** у профиля `reviewsCount` = 1 и `rating` = 4.0

#### Scenario: Subsequent review
- **WHEN** у профиля `rating` = 4.0 и `reviewsCount` = 1 и другой клиент сохраняет отзыв со `score` = 5
- **THEN** у профиля `reviewsCount` = 2 и `rating` = 4.5

#### Scenario: Rejected review leaves profile unchanged
- **WHEN** отзыв отклонён по любому правилу выше
- **THEN** `rating` и `reviewsCount` профиля не меняются

### Requirement: Reviews of a specialist are readable
Система MUST отдавать список отзывов профиля по `GET /api/specialist-reviews?profileId=` без токена, новые первыми.

#### Scenario: List reviews
- **WHEN** гость вызывает `GET /api/specialist-reviews?profileId=<id>` для профиля с двумя отзывами
- **THEN** ответ `200`, `success` равен true, в списке два отзыва с `score`, `text`, `authorId`, `createdAt`, более поздний первым

#### Scenario: No reviews
- **WHEN** гость вызывает `GET /api/specialist-reviews?profileId=<id>` для профиля без отзывов
- **THEN** ответ `200`, `success` равен true, список пуст
