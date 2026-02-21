package ru.belov.ourabroad.web.validators;

public enum ErrorCode {

    // Общие ошибки
    FIELD_REQUIRED("Поле обязательно для заполнения"),

    // Email ошибки
    EMAIL_REQUIRED("Email обязателен"),
    USER_ID_REQUIRED("userId обязателен"),
    USER_NOT_FOUND("Пользователь не найден"),
    SPECIALIST_SERVICE_NOT_FOUND("Услуга не найдена"),
    EMAIL_INVALID_FORMAT("Email имеет неверный формат. Пример: user@example.com"),
    EMAIL_ALREADY_EXISTS("Пользователь с таким email уже существует"),
    USER_ALREADY_EXISTS("Пользователь с таким id уже существует"),
    SPECIALIST_ALREADY_EXISTS("Специалист с таким id уже существует"),
    VALIDATION_ERROR("Ошибка валидации поля"),

    // Телефон ошибки
    PHONE_INVALID_FORMAT("Телефон должен быть в формате +79123456789 (10-15 цифр после +)"),
    PHONE_ALREADY_EXISTS("Пользователь с таким телефоном уже существует"),
    TELEGRAM_INVALID("Ошибка валидации Telegram-аккаунта"),
    WHATSAPP_INVALID("Ошибка валидации WhatsApp-аккаунта"),
    PRICE_MUST_BE_BIGGER_THAN_ZERO("Должно быть больше нуля"),


    // Пароль ошибки
    PASSWORD_REQUIRED("Пароль обязателен"),
    PASSWORD_LENGTH_INVALID("Пароль должен быть от 8 до 16 символов"),
    PASSWORD_UPPERCASE_REQUIRED("Пароль должен содержать хотя бы одну заглавную букву"),
    PASSWORD_DIGIT_REQUIRED("Пароль должен содержать хотя бы одну цифру"),
    PASSWORD_INVALID_CHARACTERS("Разрешены только латинские буквы, цифры и специальные символы @$!%*?&"),
    PASSWORD_WEAK("Пароль не соответствует требованиям безопасности"),
    PASSWORDS_ARE_NOT_EQUAL("Старый пароль не соответствует введённому"),

    // Имя/отображение профиля
    DISPLAY_NAME_TOO_SHORT("Имя для отображения должно содержать минимум 2 символа"),
    DISPLAY_NAME_TOO_LONG("Имя для отображения должно содержать максимум 50 символов"),

    // Профиль
    BIO_TOO_LONG("Описание не должно превышать 500 символов"),

    // Специалист
    CATEGORY_REQUIRED("Категория специалиста обязательна"),
    PRICE_RANGE_INVALID("Минимальная цена не может быть больше максимальной"),

    // Верификация
    VERIFICATION_TYPE_INVALID("Неверный тип верификации"),
    VERIFICATION_ALREADY_EXISTS("Верификация уже существует");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getMessage(Object... args) {
        return String.format(message, args);
    }

    // Можно добавить коды ошибок для клиента
    public String getCode() {
        return name().toLowerCase();
    }
}
