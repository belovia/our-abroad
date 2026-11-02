# Proposal

## Why

Тексты правил пароля уже есть в `ErrorCode`, но `UserValidator.validatePassword` отклоняет только пустое значение. Регистрация, создание пользователя и смена пароля принимают любую непустую строку, хотя норма уже называет длину, набор символов, заглавную букву и цифру.

## What Changes

- `validatePassword` применяет правила, уже записанные в текстах ошибок. Первая невыполненная проверка фиксирует ошибку, как и остальные методы `UserValidator`.
- Порядок: пустой пароль, длина 8–16, допустимые символы, хотя бы одна заглавная латинская буква, хотя бы одна цифра.
- **BREAKING**: `POST /api/auth/register`, `POST /api/users` и `PUT /api/users/me/password` больше не принимают пароль вне этих правил. Ответ остаётся `200` с `success: false` и текстом соответствующего `ErrorCode`.
- Уже сохранённый слабый пароль по-прежнему подходит для логина. Проверка силы не выполняется на `POST /api/auth/login` и не применяется к старому паролю при смене.

## Capabilities

### New Capabilities

- `password-policy`: правила нового пароля при создании учётной записи и при смене пароля.

### Modified Capabilities

- Нет. В `openspec/specs/` возможностей пока нет.

## Impact

- `UserValidator.validatePassword` и вызовы из `validateCreateUserRequest` и `ChangeUserPasswordUseCaseImpl`.
- Тексты не меняются: `PASSWORD_REQUIRED`, `PASSWORD_LENGTH_INVALID`, `PASSWORD_INVALID_CHARACTERS`, `PASSWORD_UPPERCASE_REQUIRED`, `PASSWORD_DIGIT_REQUIRED`.
- `PASSWORD_WEAK` остаётся неиспользуемым. Отдельного общего отказа нет: клиент получает первое конкретное правило.
- Строчные буквы не требуются: в существующих текстах такого правила нет.
- `docs/ssot/identity.md` и `docs/ssot/gaps.md`: после реализации GAP-002 закрывается, а правило пароля переносится в тему identity.
