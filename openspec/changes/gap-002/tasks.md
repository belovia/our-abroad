# Tasks

## 1. Правила нового пароля

- [ ] 1.1 Добавить проверки в `CreateUserUseCaseImplTest` и `ChangeUserPasswordUseCaseImplTest` на пустой пароль, длину, запрещённый символ, отсутствие заглавной буквы, отсутствие цифры, первое сообщение для `ab` и `password`, успешные `Abcdef1!` и `ABCDEFG1`, смену с коротким новым паролем и смену с верным новым и неверным текущим. Прогон этих тестов до правки `validatePassword` падает на новых случаях.
- [ ] 1.2 Реализовать шаги в `UserValidator.validatePassword` в порядке из `specs/password-policy/spec.md` и заменить пароль `password` в успешном сценарии `AuthenticationUseCaseImplRegisterProfileTest` на пароль, который проходит правила. Прогон `CreateUserUseCaseImplTest`, `ChangeUserPasswordUseCaseImplTest`, `AuthenticationUseCaseImplRegisterProfileTest` завершается успешно. Логин и `PASSWORD_WEAK` не меняются.
- [ ] 1.3 Записать правило в `docs/ssot/identity.md`, убрать GAP-002 из открытого списка `docs/ssot/gaps.md` и добавить строку Update Log. В identity виден порядок проверок и то, что логин сохранённый пароль по силе не перепроверяет.

## Workflow follow-up

- После ревью заархивировать change `gap-002`.
- Проверить, что требование `password-policy` попало в основную спеку.
