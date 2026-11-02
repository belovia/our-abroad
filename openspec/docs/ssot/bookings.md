---
id: bookings
title: Записи к специалисту
status: as-is
last_updated: 2026-10-31
---

# Записи

`Booking`: `id`, `userId` (клиент), `specialistId` (id профиля специалиста), `serviceId`, `startTime`, `status`, `createdAt`.

`BookingStatus`: `PENDING`, `CONFIRMED`, `CANCELLED`. Новая запись создаётся в `PENDING`.

`POST /api/bookings` берёт клиента из JWT. Тело: `specialistId`, `serviceId`, `startTime`. Пустое поле — «Ошибка валидации запроса». Услуга проверяется по правилу из [specialists.md](specialists.md).

Отмена (`PATCH /api/bookings/{id}/cancel`) доступна клиенту этой записи. Повторная отмена уже `CANCELLED` — «Недопустимое состояние записи». Чужой пользователь — «Доступ запрещён». Подтверждённую запись клиент отменить может: запрет стоит только на статусе `CANCELLED`.

Подтверждение (`PATCH /api/bookings/{id}/confirm`) доступно пользователю, чей `SpecialistProfile.userId` совпадает с текущим. Допустимый исходный статус — только `PENDING`.

`GET /api/bookings/my` — записи текущего клиента. `GET /api/bookings/incoming` — записи профиля специалиста текущего пользователя.

Нет записи — «Запись не найдена».

## Источник в коде

`Booking`, `BookingFactory`, `BookingStatus`, `BookingServiceImpl`, `BookingController`.

## Update Log

| Дата | Изменение |
|---|---|
| 2026-10-31 | Статусы и права записи по коду. |
