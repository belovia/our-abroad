package ru.belov.ourabroad.poi.storage.sql;

public final class RefreshTokenSql {

    private RefreshTokenSql() {
    }

    public static final String INSERT = """
            insert into refresh_tokens (
                id, user_id, token_hash, expires_at, created_at, device_info
            ) values (
                :id, :userId, :tokenHash, :expiresAt, :createdAt, :deviceInfo
            )
            """;

    public static final String FIND_BY_HASH = """
            select id, user_id, token_hash, expires_at, created_at, device_info
            from refresh_tokens
            where token_hash = :tokenHash
            """;

    public static final String DELETE_BY_ID = """
            delete from refresh_tokens where id = :id
            """;

    public static final String DELETE_BY_HASH = """
            delete from refresh_tokens where token_hash = :tokenHash
            """;

    public static final String DELETE_ALL_BY_USER = """
            delete from refresh_tokens where user_id = :userId
            """;
}
