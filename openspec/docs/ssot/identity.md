---
id: identity
title: Учётная запись и сессия
status: as-is
last_updated: 2026-11-04
---

# Identity

## User

Запись `User`: `id` (UUID), `email`, `phone`, `password` (в БД это `password_hash`), `status`, `telegramUsername`, `whatsappNumber`, `activity`, `roles`, `createdAt`, `lastLoginAt`.

`UserStatus`: `ACTIVE`, `BLOCKED`, `DELETED`. Новый пользователь создаётся со статусом `ACTIVE` и ролью `ROLE_USER` (`AppRoles.DEFAULT`). Роль хранится строкой.

Пароль кодируется BCrypt.

Email обязателен и уникален. Формат: `^[A-Za-z\d+_.-]+@[A-Za-z\d.-]+\.[A-Za-z]{2,}$`. Занятый email на регистрации даёт текст «Пользователь с таким email уже существует».

Телефон необязателен. Если передан, формат `^\+\d{10,15}$`. Уникальность задана столбцом и проверяется до записи: занятый другим пользователем телефон даёт текст «Пользователь с таким телефоном уже существует» (`PHONE_ALREADY_EXISTS`) на `POST /api/auth/register`, `POST /api/users` и `PUT /api/users/me/phone` (`200`, `success: false`). На `PATCH /api/users/me` при занятом `phone` значение не сохраняется, ответ контроллера по-прежнему `204` без тела. Свой текущий телефон конфликтом не считается. Без текста в поле уникальность не проверяется.

Telegram, если проверяется: `^@\w{5,32}$`. WhatsApp, если проверяется, тем же шаблоном, что телефон. Эти две проверки вызываются на `PATCH /api/users/me`, не на `POST /api/auth/register`.

Новый пароль проверяет `UserValidator.validatePassword` при `POST /api/auth/register`, `POST /api/users` и `PUT /api/users/me/password`. Порядок: пустота или одни пробелы → «Пароль обязателен»; длина не 8–16 → «Пароль должен быть от 8 до 16 символов»; символы вне латиницы, цифр и `@$!%*?&` → «Разрешены только латинские буквы, цифры и специальные символы @$!%*?&»; нет заглавной латинской буквы → «Пароль должен содержать хотя бы одну заглавную букву»; нет цифры → «Пароль должен содержать хотя бы одну цифру». Возвращается только первая ошибка. Строчная буква не требуется. `PASSWORD_WEAK` не используется.

`POST /api/auth/login` сохранённый пароль по силе не перепроверяет: ранее принятый короткий или слабый хеш по-прежнему подходит для входа. При смене пароля сила проверяется только у нового значения; старый сверяется с хешем.


## Два входа создания пользователя

`POST /api/auth/register` создаёт пользователя и сразу выдаёт пару токенов. Тело: `email`, `phone`, `password`, `telegramUsername`, `whatsAppNumber`, `activity`.

`POST /api/users` вызывает `CreateUserUseCase` и токены не выдаёт. Маршрут открыт.

`POST /api/auth/login`: email и пароль. Порядок проверок: пустые поля → email не найден → статус → пароль. Статус `BLOCKED` даёт «Учётная запись заблокирована» (`USER_BLOCKED`); пустые поля, неизвестный email, статус `DELETED` и несовпавший пароль дают один текст: «Неверный email или пароль». При любом отказе токены не выдаются и `lastLoginAt` не меняется. Успех обновляет `lastLoginAt` и выдаёт токены. В запрос можно передать `deviceInfo` — оно пишется в refresh-запись.

Ответ с токенами: `success`, `message`, `userId`, `accessToken`, `refreshToken`, `expiresInSeconds`, `tokenType` = `Bearer`.

## Токены

Access JWT подписывается HMAC по ключу `app.jwt.secret` (байты UTF-8). Claims: `sub` и `userId` равны id пользователя, плюс `email` и `roles`. Срок — `app.jwt.access-token-minutes` минут. Значение в `application.properties` сейчас `10000`. В тему секрет не копируется.

Refresh — 48 случайных байт в Base64 URL без padding. В таблице лежит SHA-256 hex, не сырое значение. Срок — `app.jwt.refresh-token-days` дней, сейчас `300`.

`POST /api/auth/refresh` удаляет найденную запись и выдаёт новый refresh и новый access. Пустой токен — «Недействительный refresh-токен». Хеш не найден — «Refresh-токен уже использован или отозван». Истёкший — запись удаляется, текст «Срок действия токена истёк».

`POST /api/auth/logout` удаляет запись по хешу. Пустой токен — ошибка. Отдельной проверки «хеш был в таблице» перед успехом нет.

Идентификатор вызывающего в закрытых сценариях берётся из JWT (`CurrentUserProvider.requiredUserId`).

## Кабинет

`GET /api/users/userId/{userId}` и `GET /api/users/email/{email}` открыты. `GET /api/users/by-phone?phone=` закрыт.

`PUT /api/users/me/email`, `/password`, `/phone` меняют соответствующее поле текущего пользователя. Смена пароля сверяет старый пароль с хешем. Несовпадение — «Старый пароль не соответствует введённому».

`PATCH /api/users/me` принимает `email`, `phone`, `telegramUsername`, `whatsappNumber`, `activity`. Поле попадает в запись, только если в теле оно не `null`. Формат и связанные проверки вызываются только для полей, которые в теле не `null`. Ответ контроллера — `204`, в том числе когда проверки внутри сценария уже остановили сохранение.

## Profile

Кабинетный `Profile` — отдельная запись с ключом `userId`. Поля: `displayName`, `avatarUrl`, `bio`, `country`, `city`, `languages` (в таблице строка через запятую).

Пустая строка создаётся сразу после успешного сохранения пользователя в `POST /api/auth/register` и в `POST /api/users`. Поля при этом пустые, `languages` — пустой набор.

`GET /api/users/me/profile` и `PATCH /api/users/me/profile` работают с профилем текущего пользователя и отвечают `200`. Нет строки — «Профиль не найден».

`PATCH` меняет только поля, которые в теле не `null`. `displayName` перед записью обрезается по краям: короче 2 символов — «Имя для отображения должно содержать минимум 2 символа», длиннее 50 — «Имя для отображения должно содержать максимум 50 символов». `bio` длиннее 500 символов — «Описание не должно превышать 500 символов». Пустой набор `languages` очищает список.

## Статус учётной записи

`PATCH /api/users/{userId}/block` и `PATCH /api/users/{userId}/activate` закрыты и требуют роль `ADMIN` (`@PreAuthorize`); без неё — `403`, `code: access_denied`. Ответ `200`, тело `userId`, `success`, `message`. Пустой `userId` — «userId обязателен», неизвестный — «Пользователь не найден».

`block` ставит `BLOCKED` и удаляет все refresh-записи пользователя (`revokeAllForUser`); выданный access доживает свой срок, фильтр JWT статус не проверяет. `activate` ставит `ACTIVE` и refresh-записи не трогает. Повторный вызов на том же статусе — `success: true` без побочных действий. `activate` не проверяет, что текущий статус именно `BLOCKED`.

Сценария, который выставляет `DELETED`, нет — см. [gaps.md](gaps.md).

## Источник в коде

`User`, `UserFactory`, `Profile`, `ProfileFactory`, `AuthenticationUseCaseImpl`, `RefreshTokenUseCaseImpl`, `RefreshTokenServiceImpl`, `AuthServiceImpl`, `JwtService`, `UserValidator`, `UserController`, `AuthController`, `UserUpdateUseCaseImpl`, `BlockUserUseCaseImpl`, `ActivateUserUseCaseImpl`, `GetMyProfileUseCaseImpl`, `UpdateMyProfileUseCaseImpl`, `ProfileServiceImpl`.

## Update Log

| Дата | Изменение |
|---|---|
| 2026-10-31 | Учётная запись, JWT и кабинет по коду. |
| 2026-10-31 | Кабинетный Profile создаётся вместе с пользователем; чтение и частичное обновление — `GET`/`PATCH /api/users/me/profile`. |
| 2026-11-02 | `validatePassword` применяет длину 8–16, набор символов, заглавную букву и цифру; логин сохранённый пароль по силе не перепроверяет. GAP-002 закрыт. |
| 2026-11-02 | Занятый телефон даёт `PHONE_ALREADY_EXISTS` на register/create/`PUT /me/phone`; на `PATCH /me` занятый `phone` не сохраняется при ответе `204`. GAP-003 закрыт. |
| 2026-11-04 | Админские `PATCH /api/users/{userId}/block` и `/activate`; block отзывает refresh; логин `BLOCKED` даёт `USER_BLOCKED`, `DELETED` — общий текст. GAP-004 закрыт. |
