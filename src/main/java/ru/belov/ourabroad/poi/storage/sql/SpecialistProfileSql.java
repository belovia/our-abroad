package ru.belov.ourabroad.poi.storage.sql;

public class SpecialistProfileSql {

    public static final String FIND_BY_ID = """
        select id, user_id,description,
               active, rating, reviews_count
        from specialist_profile
        where id = :id
        """;

    public static final String FIND_BY_USER_ID = """
        select id, user_id,description,
              active, rating, reviews_count
        from specialist_profile
        where user_id = :userId
        """;

    public static final String INSERT = """
        insert into specialist_profile (
            id, user_id, description,
            active, rating, reviews_count
        ) values (
            :id, :userId, :description,
            :active, :rating, :reviewsCount
        )
        """;

    public static final String UPDATE = """
        update specialist_profile
        set description = :description,
            active = :active,
            rating = :rating,
            reviews_count = :reviewsCount
        where id = :id
        """;

    public static final String DEACTIVATE = """
        update specialist_profile
        set active = false
        where id = :id
        """;

    public static final String DELETE = """
        delete from specialist_profile
        where id = :id
        """;
}
