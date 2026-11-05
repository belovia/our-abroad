# Tasks

## 1. Домен и хранилище

- [ ] 1.1 Добавить `ErrorCode.REVIEW_SCORE_INVALID`, `REVIEW_NOT_ALLOWED`, `REVIEW_ALREADY_EXISTS` с текстами из proposal; добавить `SpecialistProfile.applyReviewStats(rating, reviewsCount)` и unit-тест на него (0/0 → 4.0/1, 4.0/1 → 4.5/2). Прогон теста завершается успешно.
- [ ] 1.2 Домен `SpecialistReview` + фабрика (`bookingId`, `authorId`, `specialistId`, `score`, `text`, `createdAt`). Liquibase `015-specialist-reviews.xml` по design (UNIQUE `booking_id`, CHECK score, индекс), включить в `db.changelog-master.xml`. `SpecialistReviewRepository` (`save`, `existsByBookingId`, `findBySpecialistId` DESC по `created_at`, `averageScoreAndCount`) + SQL + маппер. Интеграционный тест на Testcontainers (по образцу `BookingRepositoryIntegrationTest`): вставка, второй отзыв по тому же booking падает на UNIQUE, `averageScoreAndCount` даёт 4.5/2. Прогон завершается успешно.

## 2. Сценарий отзыва

- [ ] 2.1 Unit-тесты `CreateSpecialistReviewUseCaseImpl`: успех с текстом и без; `PENDING`, `CANCELLED`, будущий `startTime` → `REVIEW_NOT_ALLOWED`; чужая запись → `ACCESS_DENIED`; неизвестная → `BOOKING_NOT_FOUND`; повтор → `REVIEW_ALREADY_EXISTS`; `score` 0 и 6 → `REVIEW_SCORE_INVALID`; пустой `bookingId` → `REQUEST_VALIDATION_ERROR`; при отказе `update` профиля не вызывается. Прогон до реализации падает.
- [ ] 2.2 Реализовать `CreateSpecialistReviewUseCase` по design (п. 5): после `save` взять `averageScoreAndCount`, применить `applyReviewStats`, `SpecialistProfileService.update`. Прогон тестов из 2.1 завершается успешно.

## 3. Чтение и маршруты

- [ ] 3.1 `GetSpecialistReviewsUseCase` (`profileId` обязателен → иначе `FIELD_REQUIRED`) с unit-тестом: два отзыва возвращаются новыми первыми, пустой список — `success: true`. Прогон завершается успешно.
- [ ] 3.2 `SpecialistReviewController`: `POST /api/specialist-reviews` (тело `bookingId`, `score`, `text`; ответ `reviewId`, `success`, `message`) и `GET /api/specialist-reviews?profileId=` (ответ `success`, `message`, `reviews[]`). В `SecurityConfig` открыть `GET /api/specialist-reviews`. MockMvc-тест по образцу `UserControllerStatusSecurityTest`: GET без токена → 200; POST без токена → 401 `unauthorized`; POST с токеном → 200 и вызов use case. Прогон завершается успешно.

## 4. Норма SSOT

- [ ] 4.1 В `openspec/docs/ssot/specialists.md` добавить раздел «Отзывы»: право на отзыв, один на запись, формула `rating`/`reviewsCount`, тексты ошибок, открытое чтение; убрать фразу «Сценария, который их пересчитывает, нет»; `last_updated` и Update Log.
- [ ] 4.2 В `openspec/docs/ssot/http-and-errors.md` добавить строки `POST /api/specialist-reviews` (нет) и `GET /api/specialist-reviews` (да); `persistence.md` — таблица `specialist_reviews`, если тема перечисляет таблицы; Update Log.
- [ ] 4.3 В `openspec/docs/ssot/gaps.md` закрыть GAP-005 строкой Update Log, убрать строку из таблицы, `last_updated`.

## Workflow follow-up

- После ревью заархивировать change `gap-005`.
- Проверить, что `specialist-reviews` попала в `openspec/specs/`.
