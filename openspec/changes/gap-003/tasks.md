# Tasks

## 1. Проверка существования телефона

- [ ] 1.1 Добавить в `UserRepository` / `UserService` метод `existsByPhone` (с исключением `userId` текущего пользователя) и покрыть его тестом или вызовом из use-case теста: занятый чужой телефон → true, свой телефон → false, отсутствие текста не вызывается из сценариев. Прогон нового теста до полной проводки в use case'ах падает или мок готов к `when(...).thenReturn(...)`.
- [ ] 1.2 В `CreateUserUseCaseImplTest` и тесте регистрации добавить случаи занятого телефона (`PHONE_ALREADY_EXISTS`) и создания/регистрации без телефона (успех). В `ChangeUserPhoneUseCaseImplTest` — занятый чужой телефон (отказ), свой же телефон (успех), `null` (без `PHONE_ALREADY_EXISTS`). Прогон этих тестов до правки use case'ов падает на новых случаях.

## 2. Сценарии create / register / change / patch

- [ ] 2.1 В `CreateUserUseCaseImpl` и `AuthenticationUseCaseImpl.register` после валидации и при `hasText(phone)` вызывать `existsByPhone` и ставить `PHONE_ALREADY_EXISTS` до `save`. Прогон `CreateUserUseCaseImplTest` и теста регистрации завершается успешно.
- [ ] 2.2 В `ChangeUserPhoneUseCaseImpl` после формата проверять занятость с исключением текущего `userId`; в `UserUpdateUseCaseImpl` — то же, если `phone != null` и есть текст, до `persistUser`. Добавить/обновить тест PATCH/update: занятый телефон не сохраняется, свой сохраняется. Прогон `ChangeUserPhoneUseCaseImplTest` и теста `UserUpdateUseCaseImpl` (или эквивалента) завершается успешно.

## 3. Норма SSOT

- [ ] 3.1 Записать правило уникальности телефона и ответ `PHONE_ALREADY_EXISTS` в `openspec/docs/ssot/identity.md`, закрыть GAP-003 в `openspec/docs/ssot/gaps.md` (Update Log в обоих). В identity видно: какие маршруты отвечают текстом, что PATCH по-прежнему `204` без тела, и что свой телефон не конфликт.

## Workflow follow-up

- После ревью заархивировать change `gap-003`.
- Проверить, что требование `phone-uniqueness` попало в основную спеку.
