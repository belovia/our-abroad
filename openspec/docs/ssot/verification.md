---
id: verification
title: Верификация
status: as-is
last_updated: 2026-10-31
---

# Верификация

`Verification`: `id`, `userId`, `type`, `relatedEntityId`, `status`, `createdAt`, `verifiedAt`.

`VerificationType`: `EMAIL`, `PHONE`, `DOCUMENT`, `SERVICE_USAGE`.

`VerificationStatus`: `PENDING`, `VERIFIED`, `REJECTED`. Новая заявка создаётся в `PENDING`.

`POST /api/verifications` создаёт заявку текущему пользователю. Тип обязателен, иначе «Неверный тип верификации». Вторая заявка с тем же пользователем, типом и `relatedEntityId` в статусе ожидания — «Верификация уже существует». Сценарий смотрит `hasPendingDuplicate`.

`GET /api/verifications/mine` — заявки текущего пользователя. `GET /api/verifications/by-id?id=` — одна заявка. Отдельной проверки «только владелец» на чтении по id в контроллере нет: маршрут закрыт любым access-токеном.

`PATCH /api/verifications/{id}/complete` вызывает `verify()`: статус `VERIFIED`, `verifiedAt` — текущее время. `PATCH .../reject` ставит `REJECTED` и тоже пишет `verifiedAt`. Оба пути требуют статус `PENDING`, иначе «Верификация не ожидает подтверждения». Проверки роли администратора на complete и reject нет.

Этот сценарий не меняет `User.email` и не отправляет письмо или SMS.

## Источник в коде

`Verification`, `VerificationFactory`, `VerificationType`, `VerificationStatus`, `CreateVerificationUseCaseImpl`, `CompleteVerificationUseCaseImpl`, `RejectVerificationUseCaseImpl`, `VerificationController`.

## Update Log

| Дата | Изменение |
|---|---|
| 2026-10-31 | Заявка и статусы по коду. |
