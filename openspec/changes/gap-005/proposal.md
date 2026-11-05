# Proposal

## Why

У `SpecialistProfile` есть `rating` и `reviewsCount`, они пишутся и читаются вместе с профилем, но ни один сценарий их не меняет (GAP-005): рейтинг навсегда `0`. Замысел продукта уже сказан в `about.md` и `roadmap.md`: отзыв оставляет только тот, кто реально побывал у специалиста, и из этих отзывов складывается рейтинг.

## What Changes

- Новая запись «отзыв о специалисте»: `bookingId`, `authorId`, `specialistId`, `score` (1–5), `text` (необязателен), `createdAt`. Таблица `specialist_reviews`, `booking_id` уникален.
- `POST /api/specialist-reviews` (закрыт): клиент оставляет отзыв по своей записи. Право: запись принадлежит текущему пользователю, её статус `CONFIRMED`, `startTime` уже в прошлом, отзыва по ней ещё нет. Отдельного статуса «визит состоялся» не вводится.
- После сохранения отзыва у профиля специалиста пересчитываются `rating` (среднее арифметическое `score` по всем отзывам профиля) и `reviewsCount` (число отзывов).
- `GET /api/specialist-reviews?profileId=` (открыт): список отзывов профиля.
- Новые тексты `ErrorCode`: `REVIEW_SCORE_INVALID` («Оценка должна быть от 1 до 5»), `REVIEW_NOT_ALLOWED` («Отзыв можно оставить только после подтверждённого визита»), `REVIEW_ALREADY_EXISTS` («Отзыв по этой записи уже оставлен»).
- Вне рамок: редактирование и удаление отзыва, ответ специалиста, модерация, отзыв без записи, новый статус `COMPLETED` у записи.

## Capabilities

### New Capabilities

- `specialist-reviews`: отзыв клиента после подтверждённого визита и пересчёт рейтинга и числа отзывов специалиста.

### Modified Capabilities

- Нет. `phone-uniqueness` и `account-blocking` к специалистам не относятся.

## Impact

- Домен: `SpecialistReview`, фабрика; метод на `SpecialistProfile` для применения новой оценки.
- Хранилище: Liquibase `015-specialist-reviews.xml`, `SpecialistReviewRepository` + SQL + маппер.
- Сценарии: `CreateSpecialistReviewUseCase`, `GetSpecialistReviewsUseCase`; сервис отзывов, использование `BookingService.findById` и `SpecialistProfileService.update`.
- Web: `SpecialistReviewController`, `SecurityConfig` — открыть `GET /api/specialist-reviews`.
- `ErrorCode`: три новых текста.
- SSOT после реализации: новая тема или раздел в `specialists.md`, строки маршрутов в `http-and-errors.md`, закрыть GAP-005 в `gaps.md`.
