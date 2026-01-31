package ru.belov.ourabroad.poi.storage.sql;

public class ProfileSql {

    public static final String FIND_BY_USER_ID = """
        select user_id,
               display_name,
               avatar_url,
               bio,
               country,
               city,
               languages
        from profiles
        where user_id = :userId
        """;

    public static final String INSERT = """
        insert into profiles (
            user_id,
            display_name,
            avatar_url,
            bio,
            country,
            city,
            languages
        ) values (
            :userId,
            :displayName,
            :avatarUrl,
            :bio,
            :country,
            :city,
            :languages
        )
        """;

    public static final String UPDATE = """
        update profiles
        set display_name = :displayName,
            avatar_url = :avatarUrl,
            bio = :bio,
            country = :country,
            city = :city,
            languages = :languages
        where user_id = :userId
        """;
}
