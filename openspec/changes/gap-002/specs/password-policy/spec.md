# Spec Delta

## Purpose

Задаёт, какой новый пароль можно сохранить при создании учётной записи и при смене пароля, и какой текст ошибки видит клиент.

## ADDED Requirements

### Requirement: Empty password is rejected
Система MUST отклонять пустой или состоящий из пробелов новый пароль текстом «Пароль обязателен».

#### Scenario: Register with blank password
- **WHEN** клиент вызывает `POST /api/auth/register` с паролем из пробелов
- **THEN** ответ `200`, `success` равен false, сообщение «Пароль обязателен»

#### Scenario: Create user with missing password
- **WHEN** клиент вызывает `POST /api/users` без пароля
- **THEN** ответ `200`, `success` равен false, сообщение «Пароль обязателен»

### Requirement: Password length is 8 to 16
Система MUST принимать новый пароль длиной от 8 до 16 символов включительно и MUST отклонять иную длину текстом «Пароль должен быть от 8 до 16 символов», если пароль не пустой.

#### Scenario: Password shorter than 8
- **WHEN** клиент регистрируется с паролем `Ab1!xyz`
- **THEN** ответ `200`, `success` равен false, сообщение «Пароль должен быть от 8 до 16 символов»

#### Scenario: Password longer than 16
- **WHEN** клиент задаёт новый пароль длиной 17 символов, удовлетворяющий остальным правилам
- **THEN** ответ `200`, `success` равен false, сообщение «Пароль должен быть от 8 до 16 символов»

#### Scenario: Password of length 8 and 16
- **WHEN** клиент регистрируется с паролем `Abcdef1!` или с паролем из 16 допустимых символов, где есть заглавная буква и цифра
- **THEN** ответ `200` и `success` равен true

### Requirement: Password character set
Непустой пароль допустимой длины MUST состоять только из латинских букв, цифр и символов `@$!%*?&`. Иной символ MUST давать текст «Разрешены только латинские буквы, цифры и специальные символы @$!%*?&».

#### Scenario: Space in password
- **WHEN** клиент меняет пароль на `Abcdef 1`
- **THEN** ответ `200`, `success` равен false, сообщение «Разрешены только латинские буквы, цифры и специальные символы @$!%*?&»

#### Scenario: Non-latin letter
- **WHEN** клиент регистрируется с паролем `Пароль123`
- **THEN** ответ `200`, `success` равен false, сообщение «Разрешены только латинские буквы, цифры и специальные символы @$!%*?&»

### Requirement: Password contains an uppercase letter
Пароль MUST содержать хотя бы одну заглавную латинскую букву. Иначе система MUST отвечать текстом «Пароль должен содержать хотя бы одну заглавную букву», если длина и набор символов уже допустимы.

#### Scenario: No uppercase letter
- **WHEN** клиент регистрируется с паролем `abcdefg1`
- **THEN** ответ `200`, `success` равен false, сообщение «Пароль должен содержать хотя бы одну заглавную букву»

### Requirement: Password contains a digit
Пароль MUST содержать хотя бы одну цифру. Иначе система MUST отвечать текстом «Пароль должен содержать хотя бы одну цифру», если длина, набор символов и заглавная буква уже допустимы. Строчная буква MUST NOT быть обязательной.

#### Scenario: No digit
- **WHEN** клиент регистрируется с паролем `Abcdefgh`
- **THEN** ответ `200`, `success` равен false, сообщение «Пароль должен содержать хотя бы одну цифру»

#### Scenario: Uppercase and digit without lowercase
- **WHEN** клиент регистрируется с паролем `ABCDEFG1`
- **THEN** ответ `200` и `success` равен true

### Requirement: First failed rule is the only message
Если новый пароль нарушает несколько правил, система MUST вернуть только первое из них в порядке: пустота, длина, набор символов, заглавная буква, цифра.

#### Scenario: Too short and missing uppercase and digit
- **WHEN** клиент регистрируется с паролем `ab`
- **THEN** сообщение «Пароль должен быть от 8 до 16 символов»

#### Scenario: Missing uppercase and digit
- **WHEN** клиент регистрируется с паролем `password`
- **THEN** сообщение «Пароль должен содержать хотя бы одну заглавную букву»

### Requirement: Stored password is not rechecked for strength
Логин MUST принимать ранее сохранённый пароль без повторной проверки этих правил. При смене пароля сила MUST проверяться только у нового пароля. Неверный текущий пароль при допустимом новом MUST давать текст «Старый пароль не соответствует введённому».

#### Scenario: Login with a previously stored weak password
- **WHEN** у пользователя сохранён пароль короче 8 символов и клиент вызывает `POST /api/auth/login` с этим паролем и верным email
- **THEN** ответ `200` и `success` равен true

#### Scenario: Change password with a weak new value
- **WHEN** клиент вызывает `PUT /api/users/me/password` с верным текущим паролем и новым паролем `short`
- **THEN** ответ `200`, `success` равен false, сообщение «Пароль должен быть от 8 до 16 символов», сохранённый пароль не меняется

#### Scenario: Change password with a valid new value and a wrong current value
- **WHEN** клиент вызывает `PUT /api/users/me/password` с неверным текущим паролем и новым паролем `Abcdef1!`
- **THEN** ответ `200`, `success` равен false, сообщение «Старый пароль не соответствует введённому»
