---
status: approved
approved_at: 2026-09-23
---

# Task template

Как формулировать задачу из approved-спеки.

## Обязательные поля

```markdown
## Goal
<что сделать одной фразой>

## Spec
- Sx: путь к approved спеке (обязательно)
- Use cases: список из спеки

## Done when
- [ ] acceptance criteria из спеки (перечислить)
- [ ] Swagger: эндпоинты доступны и прогнаны руками (если backend)

## Out of scope
- что не делать в этой задаче
```

## Правила

1. Задача ссылается только на `status: approved` спеки.
2. Один vertical slice / один use case (или маленькая связанная группа).
3. Не смешивать Identity с другими доменами.
4. Не изобретать поля/эндпоинты вне спеки.
