# Design

## Context

См. proposal.md, раздел Why. `DeleteSpecialistProfileUseCaseImpl` валидирует id через throw `IllegalArgumentException`, затем вызывает `SpecialistProfileService.delete`, который при отсутствии строки кидает `SpecialistServiceNotFoundException`. Владельца не проверяет. Образец HTTP и `Response(success, message)` — `SpecialistServiceController.delete` + `DeleteSpecialistServiceUseCaseImpl`. Загрузка профиля с ошибкой в `Context` уже есть: `SpecialistProfileService.findById`. `CurrentUserProvider.requiredUserId` используется в create-профиля. FK на `specialist_profile(id)` у услуг, записей и отзывов — `ON DELETE CASCADE`.

## Goals / Non-Goals

**Goals:**

- Закрытый DELETE с ответом `200`/`400` по `success`.
- Удаление только своего профиля; чужой и отсутствующий — отказ существующими текстами `ErrorCode`.
- Без новых таблиц и миграций.

**Non-Goals:**

- Soft-delete / `active = false`.
- Явный cleanup дочерних сущностей в коде (полагаемся на каскад БД).
- Проверка владельца на `PATCH` профиля и на удалении услуги.

## Decisions

1. **Контракт сценария как у услуги.** `DeleteSpecialistProfileUseCase.delete(String) → Response(boolean success, String message)`. Пустой id → `REQUEST_VALIDATION_ERROR`; не найден → `SPECIALIST_PROFILE_NOT_FOUND` («Специалист не найден»); чужой → `ACCESS_DENIED`; успех → `SUCCESS`. Throw из сценария убираем, чтобы контроллер маппил только `success`.
2. **Порядок в сценарии.** `CurrentUserProvider.requiredUserId` → валидация id → `findById(profileId, context)` → сравнение `profile.getUserId()` с текущим → `delete` (через Context или после успешной сверки). Альтернатива «сначала delete, потом проверить» отклонена: чужой id нельзя трогать.
3. **`SpecialistProfileService.delete` → Context.** Привести к виду `delete(String profileId, Context context)` по образцу `SpecialistServiceService.deleteById`: `repository.deleteById` false → `SPECIALIST_PROFILE_NOT_FOUND`, без исключения. Сигнатуру интерфейса обновить; единственный вызывающий — этот сценарий (после поиска вызывающий уже знает, что строка есть, но Context-паттерн единообразен).
4. **Маршрут.** `DELETE /api/specialist-profiles/{profileId}` в `SpecialistProfileController`; при `!response.success()` → `400`, иначе `200`. Отдельной строки в `SecurityConfig.permitAll` нет — закрыт через `anyRequest().authenticated()`.
5. **Каскад БД как единственный cleanup.** Не удаляем услуги/записи/отзывы в use case: FK уже `ON DELETE CASCADE`. Документируем в SSOT как следствие hard delete.

## Risks / Trade-offs

- [Hard delete стирает записи и отзывы клиента] → Принято пользователем (вариант A); soft-deactivate вне рамок. В SSOT явно указать каскад.
- [Пустой path-variable на HTTP маловероятен] → Сценарий валидирует blank на уровне use case; HTTP-тест фокусируется на 401 / чужой / не найден / свой.
- [Удаление услуги по-прежнему без проверки владельца] → Не расширяем scope; отдельный gap при необходимости.

## Migration Plan

Код и SSOT только; схема БД не меняется. Откат — убрать маршрут и вернуть прежний void/throw сценарий.
