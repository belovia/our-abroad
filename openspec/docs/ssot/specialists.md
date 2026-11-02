---
id: specialists
title: Специалисты и услуги
status: as-is
last_updated: 2026-10-31
---

# Специалисты

## Профиль

`SpecialistProfile`: `id`, `userId`, `description`, `active`, `rating`, `reviewsCount`, набор `services` в объекте.

Создание (`POST /api/specialist-profiles`) привязывает профиль к текущему пользователю, ставит `active = true`, `rating = 0`, `reviewsCount = 0`. В таблице `user_id` уникален. Сценарий перед вставкой повторный профиль не ищет.

Чтение по `profileId` и по `userId` открыто.

`PATCH /api/specialist-profiles` требует `profileId` и `description` и записывает описание. Поле `services` в типе запроса есть, сценарий его не применяет. Сверки «профиль принадлежит текущему пользователю» в этом сценарии нет.

`rating` и `reviewsCount` читаются и пишутся вместе с профилем. Сценария, который их пересчитывает, нет. См. [gaps.md](gaps.md).

`DeleteSpecialistProfileUseCase` есть, контроллер его не вызывает. См. [gaps.md](gaps.md).

## Услуга

`SpecialistService`: `id`, `specialistId` (это id профиля), `title`, `description`, `price` (целое), `currency`, `active`.

Создание требует непустой `title` и `price >= 0`. Валюта в запрос передаётся и при сборке записи подменяется на `USD`. Сценарий не сверяет, что профиль принадлежит текущему пользователю.

Изменение услуги отклоняет цену не больше нуля текстом «Должно быть больше нуля».

Удаление при неуспехе сценария отвечает `400`, иначе `200`.

Список `GET /api/specialist-services?profileId=` открыт.

Запись к специалисту берёт услугу только если она есть, `active` и её `specialistId` совпадает с профилем в записи. Иначе текст «Услуга недоступна». Правило записи — [bookings.md](bookings.md).

## Источник в коде

`SpecialistProfile`, `SpecialistProfileFactory`, `SpecialistService`, `CreateSpecialistProfileUseCaseImpl`, `ChangeSpecialistProfileUseCaseImpl`, `CreateSpecialistServiceUseCaseImpl`, `SpecialistServiceServiceImpl`, контроллеры `SpecialistProfileController`, `SpecialistServiceController`.

## Update Log

| Дата | Изменение |
|---|---|
| 2026-10-31 | Профиль специалиста и услуги по коду. |
