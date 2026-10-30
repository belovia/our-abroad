---
id: comments
title: Комментарии
status: as-is
last_updated: 2026-10-31
---

# Комментарии

`Comment`: `id`, `authorId`, `entityId`, `entityType`, `parentId`, `content`, `likes`, `repliesCount`, `createdAt`.

`CommentEntityType`: `ARTICLE`, `DISCUSSION`, `ANSWER`. Разбор строки — `valueOf` после trim и верхнего регистра. Пустая строка не разбирается.

`POST /api/comments` создаёт комментарий текущего пользователя. Тело: `entityId`, `entityType`, `content`, `parentId`. Пустой текст после trim — «Поле обязательно для заполнения».

Ответ разрешён только на комментарий без своего `parentId`. Родитель должен относиться к тому же `entityId` и `entityType`, иначе «Ошибка валидации запроса». Более глубокий уровень — «Превышена глубина вложенности комментариев». У родителя увеличивается `repliesCount`.

`POST /api/comments/{id}/like` переключает лайк текущего пользователя. Первый вызов создаёт `CommentLike` и делает `likes + 1`. Повторный удаляет лайк и делает `likes - 1`. Пара пользователь–комментарий уникальна. Нет комментария — «Комментарий не найден».

`GET /api/comments` закрыт. Обязательны `entityId` и `entityType`. Сортировка по умолчанию — `createdAt` по убыванию. Параметр `sort` — список `поле` или `поле,asc`. `page` и `size` оба заданы, `page >= 0` и `size > 0` — постраничная выборка, иначе без страницы.

Контроллеров статей и обсуждений в `web.controller` нет. Значения `ARTICLE` и `DISCUSSION` можно передать в комментарий. См. [gaps.md](gaps.md).

## Источник в коде

`Comment`, `CommentLike`, `CommentEntityType`, `CommentServiceImpl`, `CommentLikeServiceImpl`, `CommentController`.

## Update Log

| Дата | Изменение |
|---|---|
| 2026-10-31 | Комментарии и лайки по коду. |
