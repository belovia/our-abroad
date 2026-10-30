---
id: reputation
title: Репутация
status: as-is
last_updated: 2026-10-31
---

# Репутация

`Reputation`: ключ `userId`, `score`, `level`. Старт: `score = 0`, `level = 1`. Повторное создание для того же пользователя — «Репутация для пользователя уже существует».

Уровень: `1 + score / 100`, не ниже 1 и не выше 10. При отрицательном score в формуле уровня возвращается 1. `applyScoreDelta` не опускает score ниже 0 и затем пересчитывает уровень.

`POST /api/reputations` и `GET /api/reputations` работают с текущим пользователем.

`PATCH /api/reputations/points` требует роль `ADMIN`. Тело задаёт `userId` и `points`. `points <= 0` — «Количество очков должно быть больше нуля». Иначе вызывается `addPoints` домена (только положительная дельта).

Начисление из других сценариев идёт через `ReputationService.addPoints` и `applyScoreDelta`. Если записи репутации нет, дельта не создаёт её и ставит «Репутация не найдена». Нулевая дельта пропускается.

Очки, которые ставят сценарии Q&A, перечислены в [qa.md](qa.md). Голос меняет репутацию автора на `+1`, когда голос становится `UP`, и на `-1`, когда прежний `UP` сменяется. Голос `DOWN` сам по себе очко репутации не даёт.

## Источник в коде

`Reputation`, `ReputationFactory`, `ReputationServiceImpl`, `AddReputationPointsUseCaseImpl`, `CreateReputationUseCaseImpl`, `ReputationController`.

## Update Log

| Дата | Изменение |
|---|---|
| 2026-10-31 | Счёт, уровень и админское начисление по коду. |
