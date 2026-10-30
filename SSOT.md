# Project SSOT

Статус корпуса: **as-is**. Текст ниже описывает поведение текущего кода на `last_updated`. Целевой MVP живёт отдельно в [.agents/specs/_index.md](.agents/specs/_index.md) и сюда не копируется.

`last_updated`: 2026-10-31

Секреты, пароли БД и JWT-ключ в эти файлы не переносятся. Они остаются в `src/main/resources/application.properties`.

## Указатель

| Тема | Файл | Владеет |
|---|---|---|
| Слои | [docs/ssot/layers.md](docs/ssot/layers.md) | пакеты и направление вызовов |
| HTTP и ошибки | [docs/ssot/http-and-errors.md](docs/ssot/http-and-errors.md) | маршруты, кто открыт, коды HTTP, тексты `ErrorCode` |
| Identity | [docs/ssot/identity.md](docs/ssot/identity.md) | `User`, сессия JWT, регистрация и логин |
| Специалисты | [docs/ssot/specialists.md](docs/ssot/specialists.md) | профиль специалиста и услуги |
| Записи | [docs/ssot/bookings.md](docs/ssot/bookings.md) | `Booking` и статусы |
| Репутация | [docs/ssot/reputation.md](docs/ssot/reputation.md) | счёт, уровень, откуда берутся очки |
| Верификация | [docs/ssot/verification.md](docs/ssot/verification.md) | заявка на проверку |
| Q&A | [docs/ssot/qa.md](docs/ssot/qa.md) | вопросы, ответы, голоса |
| Комментарии | [docs/ssot/comments.md](docs/ssot/comments.md) | комментарии и лайки |
| Хранение | [docs/ssot/persistence.md](docs/ssot/persistence.md) | таблицы Liquibase |
| Пробелы | [docs/ssot/gaps.md](docs/ssot/gaps.md) | что есть в коде и не доведено до сценария |

Правила ведения: [docs/SSOT_GOVERNANCE.md](docs/SSOT_GOVERNANCE.md). Новая тема — из [docs/ssot/_templates/TOPIC_TEMPLATE.md](docs/ssot/_templates/TOPIC_TEMPLATE.md).

## Update Log

| Дата | Изменение |
|---|---|
| 2026-10-31 | Первая as-is норма по коду: слои, HTTP, identity, специалисты, записи, репутация, верификация, Q&A, комментарии, таблицы, пробелы. |
