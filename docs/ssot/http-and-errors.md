---
id: http-and-errors
title: HTTP и ошибки
status: as-is
last_updated: 2026-10-31
---

# HTTP и ошибки

Сессия сервера не создаётся. CSRF выключен. Защищённый вызов требует `Authorization: Bearer <access>`.

Невалидный или просроченный access-токен фильтр снимает с контекста и пропускает запрос дальше. Для закрытого маршрута входная точка отвечает `401` и JSON `success: false`, `message: "Требуется авторизация"`, `code: "unauthorized"`.

`403` отдаёт обработчик отказа в доступе: `code: "access_denied"`, текст `ErrorCode.ACCESS_DENIED` («Доступ запрещён»). Единственная проверка роли на методе — `PATCH /api/reputations/points`, роль `ADMIN`.

Остальные сценарии возвращают `200` и признак `success` в теле. Исключение: `DELETE /api/specialist-services/{serviceId}` при `success: false` отвечает `400`. `PATCH /api/users/me` отвечает `204` и тело use case наружу не отдаёт.

Открыты без токена: Swagger и `/v3/api-docs/**`, `/error`, `/actuator/health`, маршруты из таблицы со столбцом «открыт».

Полный перечень текстов — enum `ErrorCode`. Код для клиента — имя константы в нижнем регистре (`getCode()`).

## Маршруты

| Метод | Путь | Открыт |
|---|---|---|
| POST | `/api/auth/register` | да |
| POST | `/api/auth/login` | да |
| POST | `/api/auth/refresh` | да |
| POST | `/api/auth/logout` | да |
| POST | `/api/users` | да |
| GET | `/api/users/userId/{userId}` | да |
| GET | `/api/users/email/{email}` | да |
| GET | `/api/users/by-phone` | нет |
| PATCH | `/api/users/me` | нет |
| GET | `/api/users/me/profile` | нет |
| PATCH | `/api/users/me/profile` | нет |
| PUT | `/api/users/me/email` | нет |
| PUT | `/api/users/me/password` | нет |
| PUT | `/api/users/me/phone` | нет |
| POST | `/api/specialist-profiles` | нет |
| GET | `/api/specialist-profiles/by-id` | да |
| GET | `/api/specialist-profiles/by-user` | да |
| PATCH | `/api/specialist-profiles` | нет |
| POST | `/api/specialist-services` | нет |
| PATCH | `/api/specialist-services` | нет |
| DELETE | `/api/specialist-services/{serviceId}` | нет |
| GET | `/api/specialist-services` | да |
| POST | `/api/bookings` | нет |
| GET | `/api/bookings/my` | нет |
| GET | `/api/bookings/incoming` | нет |
| PATCH | `/api/bookings/{id}/cancel` | нет |
| PATCH | `/api/bookings/{id}/confirm` | нет |
| POST | `/api/reputations` | нет |
| GET | `/api/reputations` | нет |
| PATCH | `/api/reputations/points` | нет, роль `ADMIN` |
| POST | `/api/verifications` | нет |
| GET | `/api/verifications/by-id` | нет |
| GET | `/api/verifications/mine` | нет |
| PATCH | `/api/verifications/{id}/complete` | нет |
| PATCH | `/api/verifications/{id}/reject` | нет |
| POST | `/api/questions` | нет |
| GET | `/api/questions` | да |
| GET | `/api/questions/{id}` | да |
| GET | `/api/questions/{id}/answers` | да |
| POST | `/api/questions/{questionId}/answers` | нет |
| POST | `/api/votes` | нет |
| POST | `/api/comments` | нет |
| POST | `/api/comments/{id}/like` | нет |
| GET | `/api/comments` | нет |

Правила сценариев — в темах identity, specialists, bookings, reputation, verification, qa, comments.

## Источник в коде

`SecurityConfig`, `JwtAuthenticationFilter`, `JwtAuthenticationEntryPoint`, `JwtAccessDeniedHandler`, контроллеры в `web.controller`, `ErrorCode`.

## Update Log

| Дата | Изменение |
|---|---|
| 2026-10-31 | Маршруты и коды HTTP по контроллерам и security. |
| 2026-10-31 | Добавлены GET и PATCH `/api/users/me/profile`. |
