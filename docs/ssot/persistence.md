---
id: persistence
title: Таблицы
status: as-is
last_updated: 2026-10-31
---

# Хранение

База — PostgreSQL. Liquibase входит через `db/changelog/db.changelog-master.yaml`, тот подключает `db.changelog-master.xml`. Порядок changeset: `001` … `014`.

Параметры подключения в эту тему не копируются.

| Таблица | Ключ и столбцы | Связи |
|---|---|---|
| `users` | `id`; `email` UNIQUE NOT NULL; `phone` UNIQUE; `password_hash` NOT NULL; `status`; `created_at`; `last_login_at`; `telegram_username`; `whatsapp_number`; `activity`; `roles` varchar(512), по умолчанию `ROLE_USER` | — |
| `profile` | `user_id` PK; `display_name`; `avatar_url`; `bio`; `country`; `city`; `languages` | сценария записи нет, см. [gaps.md](gaps.md) |
| `specialist_profile` | `id` PK; `user_id` UNIQUE NOT NULL; `description`; `active`; `rating` default 0; `reviews_count` default 0 | — |
| `specialist_service` | `id` PK; `specialist_id`; `title`; `description`; `price`; `currency`; `active` | FK на `specialist_profile(id)` ON DELETE CASCADE |
| `reputation` | `user_id` PK; `score`; `level` | — |
| `verification` | `id` PK; `user_id`; `type`; `related_entity_id`; `status`; `created_at`; `verified_at` | индексы по `user_id` и `(user_id, status)` |
| `qa_questions` | `id` PK; `author_id`; `title`; `content`; `tags`; `votes` default 0; `answers_count` default 0; `created_at` | — |
| `qa_answers` | `id` PK; `question_id`; `author_id`; `content`; `votes` default 0; `accepted` default false; `created_at`; `specialist_profile_id` | — |
| `qa_votes` | `id` PK; `user_id`; `entity_id`; `vote_type` | UNIQUE (`user_id`, `entity_id`) |
| `comments` | `id` PK; `author_id`; `entity_id`; `entity_type`; `parent_id`; `content`; `likes` default 0; `replies_count` default 0; `created_at` | FK `parent_id` → `comments(id)` ON DELETE CASCADE |
| `comment_likes` | `id` PK; `user_id`; `comment_id` | FK на `comments(id)` ON DELETE CASCADE; UNIQUE (`user_id`, `comment_id`) |
| `bookings` | `id` PK; `user_id`; `specialist_id`; `service_id`; `start_time`; `status`; `created_at` | FK на `users`, `specialist_profile`, `specialist_service`, все ON DELETE CASCADE |
| `refresh_tokens` | `id` PK; `user_id`; `token_hash`; `expires_at`; `created_at`; `device_info` | FK на `users(id)` ON DELETE CASCADE; UNIQUE (`token_hash`) |

Смысл полей — в темах identity, specialists, bookings, reputation, verification, qa, comments.

## Источник в коде

`src/main/resources/db/changelog/*.xml`, `db.changelog-master.yaml`.

## Update Log

| Дата | Изменение |
|---|---|
| 2026-10-31 | Таблицы по changeset 001–014. |
