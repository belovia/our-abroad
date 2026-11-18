---
id: specialists
title: Специалисты и услуги
status: as-is
last_updated: 2026-11-18
---

# Специалисты

## Профиль

`SpecialistProfile`: `id`, `userId`, `description`, `active`, `rating`, `reviewsCount`, набор `services` в объекте.

Создание (`POST /api/specialist-profiles`) привязывает профиль к текущему пользователю, ставит `active = true`, `rating = 0`, `reviewsCount = 0`. В таблице `user_id` уникален. Сценарий перед вставкой повторный профиль не ищет.

Чтение по `profileId` и по `userId` открыто.

`PATCH /api/specialist-profiles` требует `profileId` и `description` и записывает описание. Поле `services` в типе запроса есть, сценарий его не применяет. Сверки «профиль принадлежит текущему пользователю» в этом сценарии нет.

`rating` и `reviewsCount` читаются и пишутся вместе с профилем. Их пересчитывает сценарий отзыва, см. «Отзывы».

`DELETE /api/specialist-profiles/{profileId}` закрыт. Удаляет строку профиля только если `userId` профиля совпадает с текущим пользователем. Успех — `200`, `success: true`. Чужой профиль — `400`, «Доступ запрещён». Нет профиля — `400`, «Специалист не найден». Пустой id — «Ошибка валидации запроса». Связанные услуги, записи и отзывы удаляются каскадом FK (`ON DELETE CASCADE`). Soft-deactivate нет.

## Отзывы

Отзыв (`specialist_reviews`): `id`, `bookingId` (уникален), `authorId`, `specialistId` (id профиля), `score` (1–5), `text` (необязателен), `createdAt`.

`POST /api/specialist-reviews` закрыт. Тело: `bookingId`, `score`, `text`. Ответ `200`: `reviewId`, `success`, `message`. Отзыв принимает только клиент своей записи, если статус `CONFIRMED` и `startTime` уже наступил. Иначе «Отзыв можно оставить только после подтверждённого визита» (`REVIEW_NOT_ALLOWED`). Чужой пользователь — «Доступ запрещён». Нет записи — «Запись не найдена». Повторный отзыв по той же записи — «Отзыв по этой записи уже оставлен» (`REVIEW_ALREADY_EXISTS`). `score` вне 1–5 — «Оценка должна быть от 1 до 5» (`REVIEW_SCORE_INVALID`). Пустой `bookingId` — «Ошибка валидации запроса».

После сохранения у профиля пересчитываются `reviewsCount` — число отзывов профиля и `rating` — среднее арифметическое их `score`. Отклонённый отзыв профиль не меняет.

`GET /api/specialist-reviews?profileId=` открыт. Ответ `200`: `success`, `message`, `reviews` (новые первыми: `id`, `bookingId`, `authorId`, `score`, `text`, `createdAt`). Нет отзывов — пустой список и `success: true`. Пустой `profileId` — «Поле обязательно для заполнения».

Редактирования и удаления отзыва нет.

## Услуга

`SpecialistService`: `id`, `specialistId` (это id профиля), `title`, `description`, `price` (целое), `currency`, `active`.

Создание требует непустой `title` и `price >= 0`. Валюта в запрос передаётся и при сборке записи подменяется на `USD`. Сценарий не сверяет, что профиль принадлежит текущему пользователю.

Изменение услуги отклоняет цену не больше нуля текстом «Должно быть больше нуля».

Удаление при неуспехе сценария отвечает `400`, иначе `200`.

Список `GET /api/specialist-services?profileId=` открыт.

Запись к специалисту берёт услугу только если она есть, `active` и её `specialistId` совпадает с профилем в записи. Иначе текст «Услуга недоступна». Правило записи — [bookings.md](bookings.md).

## Источник в коде

`SpecialistProfile`, `SpecialistProfileFactory`, `SpecialistReview`, `SpecialistReviewFactory`, `SpecialistService`, `CreateSpecialistProfileUseCaseImpl`, `ChangeSpecialistProfileUseCaseImpl`, `DeleteSpecialistProfileUseCaseImpl`, `CreateSpecialistServiceUseCaseImpl`, `CreateSpecialistReviewUseCaseImpl`, `GetSpecialistReviewsUseCaseImpl`, `SpecialistServiceServiceImpl`, контроллеры `SpecialistProfileController`, `SpecialistServiceController`, `SpecialistReviewController`.

## Update Log

| Дата | Изменение |
|---|---|
| 2026-10-31 | Профиль специалиста и услуги по коду. |
| 2026-11-06 | Отзыв после подтверждённого визита пересчитывает `rating` и `reviewsCount`. GAP-005 закрыт. |
| 2026-11-18 | `DELETE /api/specialist-profiles/{profileId}`: hard delete владельцем, каскад FK; GAP-006 закрыт. |
