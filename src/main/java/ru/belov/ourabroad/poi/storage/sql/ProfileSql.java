package ru.belov.ourabroad.poi.storage.sql;

import java.util.Set;

public class ProfileSql {

    public static String joinLanguages(Set<String> languages) {
        if (languages == null || languages.isEmpty()) {
            return "";
        }
        return String.join(",", languages);
    }

    public static final String FIND_BY_USER_ID = """
        select user_id,
               display_name,
               avatar_url,
               bio,
               country,
               city,
               languages
        from profile
        where user_id = :userId
        """;

    public static final String INSERT = """
        insert into profile (
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
