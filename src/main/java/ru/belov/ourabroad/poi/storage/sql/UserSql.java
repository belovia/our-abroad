package ru.belov.ourabroad.poi.storage.sql;

public class UserSql {

    public static final String FIND_BY_ID = """
        select id,
               email,
               phone,
               password_hash,
               status,
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
               created_at,
               last_login_at
        from users
        where email = :email
        """;

    public static final String INSERT = """
        insert into users (
            id,
            email,
            phone,
            password_hash,
            status,
            created_at,
            last_login_at
        ) values (
            :id,
            :email,
            :phone,
            :passwordHash,
            :status,
            :createdAt,
            :lastLoginAt
        )
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
}
