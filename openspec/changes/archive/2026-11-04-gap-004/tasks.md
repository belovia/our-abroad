# Tasks

## 1. Логин заблокированного

- [x] 1.1 Добавить `ErrorCode.USER_BLOCKED` с текстом «Учётная запись заблокирована» и в тесте `AuthServiceImpl` (или существующем auth-тесте) зафиксировать: `BLOCKED` → этот текст, `DELETED` → `INVALID_CREDENTIALS`, верный `ACTIVE` → пользователь. Прогон новых случаев до правки сервиса падает.
- [x] 1.2 В `AuthServiceImpl.authenticateByEmailAndPassword` после нахождения пользователя: `BLOCKED` → `USER_BLOCKED`, любой другой не-`ACTIVE` → `INVALID_CREDENTIALS`. Прогон теста из 1.1 завершается успешно; `lastLoginAt` при отказе не обновляется (проверка на уровне login use case или сервиса).

## 2. Use case'ы block / activate

- [x] 2.1 Добавить unit-тесты `BlockUserUseCaseImpl` / `ActivateUserUseCaseImpl`: успех ACTIVE→BLOCKED и BLOCKED→ACTIVE; неизвестный id → `USER_NOT_FOUND`; повторный block/activate идемпотентен (`success: true`); block вызывает `revokeAllForUser`, activate — нет. Прогон до реализации падает или моки готовы к `when(...).thenReturn(...)`.
- [x] 2.2 Реализовать `BlockUserUseCase` / `ActivateUserUseCase` (`validateId` → `findById` → `block`/`activate` → `update`; только block → `revokeAllForUser`), `@Transactional`. Прогон тестов из 2.1 завершается успешно.

## 3. HTTP-маршруты

- [x] 3.1 В `UserController` добавить `PATCH /api/users/{userId}/block` и `PATCH /api/users/{userId}/activate` с `@PreAuthorize("hasRole('ADMIN')")`, ответ `200` и тело `{ userId, success, message }`. Покрыть тестом или MockMvc: `ROLE_ADMIN` → 200 и смена статуса; `ROLE_USER` → 403 `access_denied` без смены статуса. Прогон этих проверок завершается успешно. `SecurityConfig` не менять.

## 4. Норма SSOT

- [x] 4.1 В `openspec/docs/ssot/identity.md` описать маршруты block/activate, отзыв refresh при block, текст `USER_BLOCKED` на логине, поведение `DELETED` на логине; убрать фразу, что у `block`/`activate` нет HTTP-маршрута. Update Log. В теме видно: кто может вызывать, идемпотентность, что activate не трогает refresh.
- [x] 4.2 В `openspec/docs/ssot/http-and-errors.md` добавить две строки маршрутов (закрыты, роль `ADMIN`) и обновить упоминание ролевых проверок (не только reputations/points). Update Log.
- [x] 4.3 В `openspec/docs/ssot/gaps.md` закрыть GAP-004 (Update Log) и открыть новый GAP: в коде есть статус `DELETED`, сценария, который его выставляет, нет. Статус нового GAP — `open`.

## Workflow follow-up

- После ревью заархивировать change `gap-004`.
- Проверить, что требование `account-blocking` попало в основную спеку `openspec/specs/account-blocking/`.
