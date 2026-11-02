# Matt Pocock skills + OpenSpec

Как вести работу в этом репо: skills задают мышление и исполнение, OpenSpec — канонические артефакты изменения, SSOT — as-is норму продукта.

## Кто за что отвечает

| Слой | Где | Роль |
|---|---|---|
| Product norm (as-is) | `SSOT.md`, `openspec/docs/ssot/` | Что код делает сейчас. Не заменяется OpenSpec/скиллами. |
| Change artifacts | `openspec/changes/<name>/` | Proposal, delta-specs, design, tasks для одного изменения. |
| Main specs | `openspec/specs/` | Утверждённые capability-спеки после archive/sync. |
| Scratch tracker | `.scratch/<feature>/` | Локальные issues/tickets для `to-spec` / `to-tickets` / `triage` / `wayfinder`. |
| Glossary / ADR | `GLOSSARY.md`, `docs/adr/` | Язык и решения, которые пишут `grill-with-docs` / `domain-modeling`. |

Правило: одно определение — в одном месте. Второй файл даёт ссылку.

## Когда что звать

### 1. Неясная идея / нужно прояснить

1. `grill-with-docs` — жёсткое интервью; по ходу пишет glossary/ADR.
2. Если нужен runnable ответ — `prototype` (через `handoff` в отдельную сессию).
3. Очень большой туманный объём — `wayfinder`, потом выход в spec/OpenSpec.

Альтернатива внутри OpenSpec: `openspec-explore` — думать и уточнять **без кода**. Не смешивать explore с реализацией.

### 2. Зафиксировать изменение (планирование)

**Предпочтительно для этого репо — OpenSpec:**

```text
openspec-propose   # или: openspec propose / opsx propose
```

Создаёт `proposal.md`, `design.md`, `specs/.../spec.md`, `tasks.md` в `openspec/changes/<name>/`.  
Граница: propose **только планирует**, код не пишет.

Matt-скиллы `to-spec` / `to-tickets` полезны, когда хочется scratch-тикеты под локальный tracker (`.scratch/`) или tracer-bullet нарезку вне OpenSpec. Для продуктовых изменений с delta-specs используй OpenSpec; scratch можно вести параллельно как рабочую очередь.

Перед propose сверься с SSOT. Если идея противоречит теме — сначала правка темы (или gap), потом артефакты.

### 3. Реализация

```text
openspec-apply-change   # или: openspec apply / opsx apply
```

Идёт по `tasks.md`. Внутри задач уместны:

- `tdd` — red/green по одному срезу
- `implement` — если задача уже самодостаточна
- `diagnosing-bugs` — если упёрлись в сложный баг
- `codebase-design` — когда проектируешь шов/модуль

Не дублируй OpenSpec `tasks.md` полным вторым набором `.scratch/.../issues`, если только не нужен отдельный AFK/параллельный контур.

### 4. После кода

1. Обнови SSOT-тему, если изменилось поведение/контракт/схема.
2. `code-review` — Standards + Spec (spec = OpenSpec change / tickets).
3. `pr` — тело PR.
4. `openspec-archive-change` / sync — когда change принят.
5. `retro` — улучшить окружение агента (правила, чеки, pointers), не код.

### 5. Входящий хаос (баги, чужие запросы)

`triage` → статус в `.scratch/...` → потом `openspec-propose` (если это продуктовое изменение) или сразу `implement` / `diagnosing-bugs` для чистого бага без смены нормы.

## Типовой happy path

```text
ask-matt                     # если неясно, с чего начать
        │
grill-with-docs              # прояснить + glossary/ADR
        │
openspec-propose             # proposal / design / delta-specs / tasks
        │
openspec-apply-change        # + tdd по задачам
        │
SSOT update (если нужно)
        │
code-review → pr
        │
openspec-archive-change
        │
retro
```

Короткий путь (маленький баг без смены спеки): `diagnosing-bugs` → fix + regression test → SSOT/gap при необходимости → `pr`.

## Чего не делать

- Не писать продукт-норму только в OpenSpec proposal, минуя SSOT.
- Не начинать `openspec-apply` в той же сессии, что и propose, пока явно не попросили implement.
- Не triage-ить тикеты, которые уже нарезал `to-tickets` / OpenSpec tasks — они уже agent-ready.
- Не создавать второй glossary, копирующий SSOT; glossary — язык, SSOT — поведение.

## Быстрые команды

| Хочу | Сказать агенту |
|---|---|
| Не знаю skill | `ask-matt` |
| Прояснить идею | `grill-with-docs` |
| OpenSpec change | `openspec propose …` |
| Думать без кода | `openspec explore` |
| Кодить change | `openspec apply …` |
| TDD-срез | `tdd` |
| Ревью ветки | `code-review since main` |
| Передать сессию | `handoff` |
| Большой туман | `wayfinder` |

## Конфиг этого репо

- Tracker: `docs/agents/issue-tracker.md` (local `.scratch/`)
- Labels: `docs/agents/triage-labels.md`
- Domain: `docs/agents/domain.md`
- Skills index: `AGENTS.md` → Agent skills
- Matt skills on disk: `.agents/skills/`
- OpenSpec skills: `.cursor/skills/openspec-*`
