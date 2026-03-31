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
        on conflict (user_id) do update set
            display_name = excluded.display_name,
            avatar_url = excluded.avatar_url,
            bio = excluded.bio,
            country = excluded.country,
            city = excluded.city,
            languages = excluded.languages
        """;
}
