# Tasks

## 1. Сценарий удаления

- [x] 1.1 Unit-тесты `DeleteSpecialistProfileUseCaseImpl`: свой профиль → `success: true` и вызов `delete`; чужой → `ACCESS_DENIED`, `delete` не вызывается; не найден → `SPECIALIST_PROFILE_NOT_FOUND`; blank id → `REQUEST_VALIDATION_ERROR`. Прогон до реализации падает (или красный после смены контракта на `Response`).
- [x] 1.2 Перевести `DeleteSpecialistProfileUseCase` на `Response(success, message)`; в impl — `CurrentUserProvider`, `findById` + сверка владельца, затем `SpecialistProfileService.delete(profileId, context)` без throw. Сигнатуру `SpecialistProfileService.delete` привести к Context (как `deleteById` у услуги). Прогон тестов из 1.1 завершается успешно.

## 2. HTTP

- [x] 2.1 `DELETE /api/specialist-profiles/{profileId}` в `SpecialistProfileController`: при `!success` → `400`, иначе `200`. MockMvc: без токена → `401`; владелец → `200` и вызов use case; при `success: false` от use case → `400`. Прогон завершается успешно.

## 3. Норма SSOT

- [x] 3.1 В `openspec/docs/ssot/specialists.md` описать hard delete владельцем, каскад услуг/записей/отзывов, тексты отказов; убрать отсылку к GAP-006; `last_updated` и Update Log.
- [x] 3.2 В `openspec/docs/ssot/http-and-errors.md` добавить `DELETE /api/specialist-profiles/{profileId}` (закрыт) и исключение `400` при `success: false` рядом с удалением услуги; в `gaps.md` закрыть GAP-006; Update Log.

## Workflow follow-up

- После ревью заархивировать change `gap-006`.
- Проверить, что `specialist-profile-deletion` попала в `openspec/specs/`.
