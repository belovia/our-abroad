# Proposal

## Why

`DeleteSpecialistProfileUseCaseImpl` уже есть и жёстко удаляет строку профиля, но HTTP-маршрута нет (GAP-006): владелец не может убрать профиль специалиста через API. Сценарий не сверяет владельца и при ошибках бросает исключения вместо ответа `success`/`message`, как у удаления услуги.

## What Changes

- Закрытый маршрут `DELETE /api/specialist-profiles/{profileId}` в `SpecialistProfileController`: вызывает сценарий удаления и при `success: false` отвечает `400`, иначе `200` (как `DELETE /api/specialist-services/{serviceId}`).
- Сценарий принимает текущего пользователя (`CurrentUserProvider`), загружает профиль, сверяет `profile.userId` с JWT subject; чужой или отсутствующий профиль — отказ без удаления.
- Контракт сценария: `Response(success, message)` вместо `void` и исключений на валидации/«не найден».
- Hard delete строки `specialist_profile`; связанные услуги, записи и отзывы уходят каскадом FK (`ON DELETE CASCADE`) — поведение БД уже так устроено.
- Вне рамок: soft-deactivate (`active = false`), админское удаление чужого профиля, отдельный cleanup без каскада, смена модели `PATCH` профиля на проверку владельца.

## Capabilities

### New Capabilities

- `specialist-profile-deletion`: владелец жёстко удаляет свой профиль специалиста через HTTP; чужой и отсутствующий профиль отклоняются.

### Modified Capabilities

- Нет. `specialist-reviews`, `account-blocking` и `phone-uniqueness` к удалению профиля не относятся.

## Impact

- `DeleteSpecialistProfileUseCase` / `DeleteSpecialistProfileUseCaseImpl`: `Response`, проверка владельца, отказ через `Context`/`ErrorCode` вместо throw.
- `SpecialistProfileService.delete` — привести к Context-паттерну (как `SpecialistServiceService.deleteById`) или оставить вызов после успешной сверки владельца.
- Web: `DELETE` в `SpecialistProfileController`; маршрут закрыт по умолчанию (`authenticated()`).
- SSOT после реализации: раздел удаления в `specialists.md`, строка маршрута и исключение `400` в `http-and-errors.md`, закрыть GAP-006 в `gaps.md`.
