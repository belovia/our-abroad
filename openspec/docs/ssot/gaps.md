---
id: gaps
title: Пробелы нормы
status: as-is
last_updated: 2026-11-02
---

# Пробелы

Здесь только то, что в коде видно и сценарием не закрыто. Чем API «должен» отвечать, не дописывается.

| id | Тема | Что видно в коде | Чего нет | Статус |
|---|---|---|---|---|
| GAP-004 | [identity](identity.md) | `User.block`, `User.activate`, статусы `BLOCKED` и `DELETED` | HTTP-сценария смены статуса. Логин любого не-`ACTIVE` отвечает как при неверном пароле | open |
| GAP-005 | [specialists](specialists.md) | Поля `rating` и `reviewsCount` читаются и сохраняются | Сценария, который меняет рейтинг или число отзывов | open |
| GAP-006 | [specialists](specialists.md) | `DeleteSpecialistProfileUseCaseImpl` | Вызова из контроллера | open |
| GAP-007 | [qa](qa.md) | `AcceptAnswerUseCaseImpl` — Spring-бин, начисляет 50 и 2 очка | HTTP-маршрута. Принятие ответа снаружи не вызывается | open |
| GAP-008 | [comments](comments.md) | `CommentEntityType.ARTICLE` и `DISCUSSION` | Контроллера статей или обсуждений | open |

## Update Log

| Дата | Изменение |
|---|---|
| 2026-10-31 | Открыты GAP-001 … GAP-008 по срезу кода. |
| 2026-10-31 | GAP-001 закрыт: профиль кабинета пишется при создании пользователя и читается через `/api/users/me/profile`. |
| 2026-11-02 | GAP-002 закрыт: `validatePassword` применяет правила длины, набора символов, заглавной буквы и цифры; норма перенесена в [identity.md](identity.md). |
| 2026-11-02 | GAP-003 закрыт: занятый телефон даёт `PHONE_ALREADY_EXISTS` на register/create/`PUT /me/phone`; на `PATCH /me` занятый `phone` не сохраняется; норма в [identity.md](identity.md). |
