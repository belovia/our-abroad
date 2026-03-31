package ru.belov.ourabroad.poi.storage.sql;

public class UserSql {

    public static final String FIND_BY_ID = """
            select id,
                   email,
                   phone,
                   password_hash,
                   status,
                   telegram_username,
                   whatsapp_number,
                   activity,
                   created_at,
                   last_login_at
            from users
            where id = :id
            """;

    public static final String FIND_BY_EMAIL = """
            select id,
                   email,
                   phone,
                   password_hash,
                   status,
                   telegram_username,
                   whatsapp_number,
                   activity,
                   created_at,
                   last_login_at
            from users
            where email = :email
            """;

    public static final String UPSERT = """
            insert into users (
                id,
                email,
                phone,
                password_hash,
                status,
                telegram_username,
                whatsapp_number,
                activity,
                created_at,
                last_login_at
            ) values (
                :id,
                :email,
                :phone,
                :passwordHash,
                :status,
                :telegramUsername,
                :whatsappNumber,
                :activity,
                :createdAt,
                :lastLoginAt
            )
            on conflict (id) do update set
                email = excluded.email,
                phone = excluded.phone,
                password_hash = excluded.password_hash,
                status = excluded.status,
                telegram_username = excluded.telegram_username,
                whatsapp_number = excluded.whatsapp_number,
                activity = excluded.activity,
                last_login_at = excluded.last_login_at
            returning (xmax = 0) as inserted
            """;

    public static final String UPDATE_LAST_LOGIN = """
            update users
            set last_login_at = :lastLoginAt
            where id = :id
            """;

    public static final String UPDATE_STATUS = """
            update users
            set status = :status
            where id = :id
            """;

    public static final String UPDATE_SOCIALS = """
            update users
            set telegram_username = :telegramUsername,
                whatsapp_number = :whatsappNumber,
                activity = :activity
            where id = :id
            """;
}
