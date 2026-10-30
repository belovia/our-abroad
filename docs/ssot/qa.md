---
id: qa
title: Вопросы и ответы
status: as-is
last_updated: 2026-10-31
---

# Q&A

## Вопрос

`Question`: `id`, `authorId`, `title`, `content`, `tags`, `votes`, `answersCount`, `createdAt`. Автор — текущий пользователь. Создание даёт автору `5` очков репутации, если запись репутации уже есть. Иначе см. [reputation.md](reputation.md).

Список `GET /api/questions` открыт. Параметры `page`, `size`, `sort` (`NEWEST` или `VOTES`), `tag`. Без `page` или `size`, либо при `page < 0` или `size <= 0`, выборка без страницы. Сортировка по умолчанию и для `NEWEST` — `createdAt` по убыванию. `VOTES` — `votes` по убыванию. Пустой `tag` не фильтрует.

`GET /api/questions/{id}` открыт. Нет вопроса — «Вопрос не найден».

## Ответ

`Answer`: `id`, `questionId`, `authorId`, `specialistProfileId`, `content`, `votes`, `accepted`, `createdAt`.

`POST /api/questions/{questionId}/answers` пишет ответ от текущего пользователя. `specialistProfileId` можно не передавать. Если передан, профиль должен существовать и его `userId` должен совпасть с автором, иначе «Недостаточно прав». Создание даёт автору `10` очков репутации при той же оговорке, что у вопроса.

`GET /api/questions/{id}/answers` открыт. Порядок: принятый ответ раньше остальных, затем `votes` по убыванию, затем `createdAt`.

## Голос

`POST /api/votes`. Тело: `target` (`QUESTION` или `ANSWER`), `entityId`, `voteType` (`UP` или `DOWN`). Один пользователь — один голос на сущность (уникальная пара в таблице).

Повтор того же типа ничего не меняет. Смена типа обновляет строку. Вклад в счётчик сущности: `UP` = `+1`, `DOWN` = `-1`; при смене в счётчик пишется разница. Репутация автора — в [reputation.md](reputation.md).

Пустые `target` или `voteType` — «Ошибка валидации запроса». Нет ответа — «Ответ не найден». Сбой записи голоса — «Не удалось сохранить голос». Сбой счётчика — «Не удалось обновить счётчик голосов».

## Принятие ответа

`AnswerService.acceptAnswer` разрешает принятие автору вопроса, снимает прежний принятый ответ на этом вопросе и при повторном принятии того же ответа ничего не меняет. `AcceptAnswerUseCaseImpl` за принятие начисляет автору ответа `50` очков и автору вопроса `2`. Контроллер этот сценарий не вызывает. См. [gaps.md](gaps.md).

## Источник в коде

`Question`, `Answer`, `Vote`, `VoteType`, `QaVoteTarget`, `QaReputationRules`, `CreateQuestionUseCaseImpl`, `AnswerQuestionUseCaseImpl`, `VoteUseCaseImpl`, `VoteServiceImpl`, `AnswerServiceImpl`, `GetQuestionsUseCaseImpl`, `AcceptAnswerUseCaseImpl`, `QaController`.

## Update Log

| Дата | Изменение |
|---|---|
| 2026-10-31 | Вопросы, ответы и голоса по коду. |
