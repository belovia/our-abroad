# Design

## Context

См. proposal.md, раздел Why. Сейчас `SpecialistProfile.rating`/`reviewsCount` — обычные поля с сеттерами, пишутся через UPSERT в `SpecialistProfileSql`. `Booking` хранит `userId`, `specialistId` (id профиля), `startTime`, `status` (`PENDING`/`CONFIRMED`/`CANCELLED`); `BookingService.findById` и `SpecialistProfileService.findById/update` уже есть. Образец закрытого POST + открытого GET по `profileId` — `SpecialistServiceController` и правило `GET /api/specialist-services` в `SecurityConfig`. Миграции — Liquibase XML, последняя `014-refresh-tokens.xml`.

## Goals / Non-Goals

**Goals:**

- Отзыв только по собственной подтверждённой записи с прошедшим `startTime`; один отзыв на запись.
- Рейтинг — производная от отзывов, пересчитывается в том же сценарии и той же транзакции.
- Открытое чтение отзывов профиля.

**Non-Goals:**

- Не вводить статус `COMPLETED` и маршрут завершения записи.
- Не редактировать и не удалять отзыв; не отвечать на отзыв; не модерировать.
- Не менять контракт профиля специалиста: `rating`/`reviewsCount` по-прежнему отдаются в ответе профиля.
- Не пересчитывать рейтинг по уже существующим данным (отзывов пока нет).

## Decisions

1. **«Побывал» = `CONFIRMED` и `startTime <= now`.** Выбрано пользователем вместо нового статуса: не требует действия специалиста после визита и не меняет `BookingStatus`. Время берётся на сервере (`LocalDateTime.now()`), как в `Booking.createdAt`.
2. **Один отзыв на запись, не на пару клиент–специалист.** UNIQUE на `specialist_reviews.booking_id` плюс предварительная проверка `existsByBookingId` → `REVIEW_ALREADY_EXISTS`. Повторный визит даёт право на новый отзыв.
3. **Пересчёт — среднее по всем отзывам, а не инкремент.** После вставки `SpecialistReviewRepository.averageScoreAndCount(specialistId)` одним запросом (`AVG(score)`, `COUNT(*)`), затем `profile.applyReviewStats(avg, count)` и `SpecialistProfileService.update`. Инкрементальная формула отклонена: накапливает ошибку округления и ломается при будущем удалении отзыва.
4. **Метод домена `SpecialistProfile.applyReviewStats(double rating, int reviewsCount)`** вместо прямых сеттеров в сценарии — остальной код профиля сеттеры использует, но правило «рейтинг только из отзывов» лучше читать в одном месте.
5. **Сценарии.** `CreateSpecialistReviewUseCaseImpl` (`@Transactional`): `CurrentUserProvider.requiredUserId` → валидация `bookingId`/`score` → `bookingService.findById` → владелец/статус/время → `existsByBookingId` → `save` → пересчёт → `update` профиля. `GetSpecialistReviewsUseCaseImpl`: `findBySpecialistId` с сортировкой `created_at DESC`. Оба в `api.usecases.create.review` / `api.usecases.get.review` по текущей раскладке пакетов.
6. **Маршруты.** `POST /api/specialist-reviews` закрыт (`anyRequest().authenticated()`); `GET /api/specialist-reviews` добавляется в `permitAll` в `SecurityConfig` рядом с `GET /api/specialist-services`. Ответ POST: `reviewId`, `success`, `message`. Ответ GET: `success`, `message`, `reviews[]`.
7. **Таблица `specialist_reviews`.** `id` VARCHAR(36) PK, `booking_id` UNIQUE FK → bookings ON DELETE CASCADE, `author_id` FK → users, `specialist_id` FK → specialist_profile ON DELETE CASCADE, `score` SMALLINT NOT NULL CHECK 1..5, `text` TEXT NULL, `created_at` TIMESTAMP NOT NULL; индекс по `specialist_id`.

## Risks / Trade-offs

- [Гонка двух одновременных POST по одной записи] → UNIQUE на `booking_id` останавливает вторую вставку; явный маппинг исключения не делается (как в GAP-003).
- [Специалист подтвердил, визит фактически не состоялся] → Принято: без статуса «завершено» отличить нельзя; клиент может отменить запись до визита, тогда отзыв невозможен.
- [Клиент сам себе специалист] → Не запрещается отдельно: запись на самого себя и так возможна по текущей норме bookings.
- [`startTime` сравнивается с серверным временем без зоны] → Совпадает с тем, как хранится `start_time`; отдельная зона не вводится.

## Migration Plan

Liquibase `015-specialist-reviews.xml`, включить в `db.changelog-master.xml`. Данных для миграции нет. Откат — удалить таблицу и маршруты; `rating`/`reviewsCount` остаются в профиле как были.
