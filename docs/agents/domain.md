# Domain Docs

How the engineering skills should consume this repo's domain documentation when exploring the codebase.

## Before exploring, read these

1. **`SSOT.md`** at the repo root — указатель as-is нормы продукта. Темы живут в `openspec/docs/ssot/`. Это канон текущего поведения кода; OpenSpec change и комментарии её не заменяют. Правила: `openspec/docs/SSOT_GOVERNANCE.md`.
2. **`GLOSSARY.md`** at the repo root, if it exists — shared vocabulary for agent skills (`grill-with-docs`, `domain-modeling`, tickets, specs).
3. **`GLOSSARY-MAP.md`** at the repo root if it exists: it points at one `GLOSSARY.md` per context. Read each one relevant to the topic.
4. **`docs/adr/`**: read ADRs that touch the area you're about to work in.

If `GLOSSARY.md` or `docs/adr/` don't exist yet, **proceed silently** for those. Don't flag their absence; don't suggest creating them upfront. The `/domain-modeling` skill (reached via `/grill-with-docs` and `/improve-codebase-architecture`) creates them lazily when terms or decisions actually get resolved.

**SSOT is different:** if the work changes status, error text, schema, package role, or product boundary, update the owning topic from `SSOT.md` (or record a gap in `openspec/docs/ssot/gaps.md`). Do not invent product norms in OpenSpec or glossary to paper over a missing SSOT fact.

## File structure

Single-context repo:

```
/
├── SSOT.md
├── GLOSSARY.md                 ← created lazily by domain-modeling
├── docs/
│   ├── agents/                 ← tracker + skill config
│   └── adr/                    ← created lazily by domain-modeling
├── openspec/
│   ├── docs/ssot/              ← SSOT topics (as-is product norm)
│   ├── changes/                ← active OpenSpec changes
│   └── specs/                  ← main capability specs
└── .scratch/                   ← local issue tracker
```

## Use the vocabulary

When your output names a domain concept (in an issue title, a refactor proposal, a hypothesis, a test name):

- Prefer terms defined in `GLOSSARY.md` when present.
- For product behaviour, statuses, HTTP contracts, and persistence, follow the owning SSOT topic.
- Don't drift to synonyms the glossary or SSOT explicitly avoids.

If the concept isn't in the glossary yet, that's a signal for `/domain-modeling`. If the behaviour isn't in SSOT, that's a gap — don't close it by guessing.

## Flag conflicts

If your output contradicts an existing ADR or SSOT topic, surface it explicitly rather than silently overriding:

> _Contradicts ADR-0007 / SSOT identity.md, but worth reopening because…_
